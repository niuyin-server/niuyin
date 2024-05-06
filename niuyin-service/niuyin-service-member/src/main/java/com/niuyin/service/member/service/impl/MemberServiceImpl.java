package com.niuyin.service.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.exception.CustomException;
import com.niuyin.common.cache.service.RedisService;
import com.niuyin.common.core.utils.IdUtils;
import com.niuyin.common.core.utils.IpUtils;
import com.niuyin.common.core.utils.JwtUtil;
import com.niuyin.common.core.utils.ServletUtils;
import com.niuyin.common.core.utils.audit.SensitiveWordUtil;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.feign.social.RemoteSocialService;
import com.niuyin.feign.video.RemoteVideoService;
import com.niuyin.model.common.enums.HttpCodeEnum;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.model.member.domain.UserSensitive;
import com.niuyin.model.member.dto.LoginUserDTO;
import com.niuyin.model.member.dto.RegisterBody;
import com.niuyin.model.member.dto.SmsRegisterDTO;
import com.niuyin.model.member.dto.UpdatePasswordDTO;
import com.niuyin.service.member.constants.UserCacheConstants;
import com.niuyin.service.member.mapper.MemberMapper;
import com.niuyin.service.member.service.IMemberInfoService;
import com.niuyin.service.member.service.IMemberService;
import com.niuyin.service.member.service.IUserSensitiveService;
import com.niuyin.service.member.service.cache.MemberRedisBatchCache;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.niuyin.model.behave.mq.BehaveQueueConstant.BEHAVE_EXCHANGE;
import static com.niuyin.model.behave.mq.BehaveQueueConstant.CREATE_ROUTING_KEY;
import static com.niuyin.model.common.enums.HttpCodeEnum.*;

/**
 * 用户表(User)表服务实现类
 *
 * @author roydon
 * @since 2023-10-24 19:18:26
 */
@Service("memberServiceImpl")
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {
    @Resource
    private MemberMapper memberMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private IUserSensitiveService userSensitiveService;

    @Resource
    private RemoteSocialService remoteSocialService;

    @Resource
    RemoteVideoService remoteVideoService;

    @Resource
    IMemberInfoService memberInfoService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private MemberRedisBatchCache memberRedisBatchCache;

    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    @Override
    public Member queryById(Long userId) {
        return this.getById(userId);
    }

    @Override
    public List<Member> queryInIds(List<Long> userIds) {
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Member::getUserId, userIds);
        return this.list(queryWrapper);
    }

    @Override
    public String login(LoginUserDTO loginUserDTO) {
        //1.检查参数
        if (StringUtils.isBlank(loginUserDTO.getUsername()) || StringUtils.isBlank(loginUserDTO.getPassword())) {
            throw new CustomException(SYSTEM_ERROR);
        }
        //2.查询用户
        Member dbUser = getOne(Wrappers.<Member>lambdaQuery().eq(Member::getUserName, loginUserDTO.getUsername()));
        if (dbUser == null) {
            throw new CustomException(USER_NOT_EXISTS);
        }
        //3.比对密码
        String salt = dbUser.getSalt();
        String pswd = loginUserDTO.getPassword();
        pswd = DigestUtils.md5DigestAsHex((pswd + salt).getBytes());
        if (pswd.equals(dbUser.getPassword())) {
            // TODO 异步记录登录信息
            recordLoginUserInfo(dbUser.getUserId());
            return JwtUtil.getToken(dbUser.getUserId());
        } else {
            throw new CustomException(PASSWORD_ERROR);
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    @Async
    public void recordLoginUserInfo(Long userId) {
        Member user = new Member();
        user.setUserId(userId);
        user.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        user.setLoginDate(LocalDateTime.now());
        this.updateById(user);
    }

    @Override
    public boolean register(RegisterBody registerBody) {
        //对数据进行非空判断
        if (StringUtils.isEmpty(registerBody.getUsername())) {
            throw new CustomException(USERNAME_NOT_NULL);
        }
        if (StringUtils.isEmpty(registerBody.getPassword())) {
            throw new CustomException(PASSWORD_NOT_NULL);
        }
        //判断username是否存在
        if (userNameExist(registerBody.getUsername())) {
            throw new CustomException(USERNAME_EXIST);
        }
        if (!registerBody.getPassword().equals(registerBody.getConfirmPassword())) {
            throw new CustomException(CONFIRM_PASSWORD_NOT_MATCH);
        }
        // 敏感词校验
        if (sensitiveCheck(registerBody.toString())) {
            throw new CustomException(HttpCodeEnum.SENSITIVEWORD_ERROR);
        }
        // 生成随机盐加密密码
        String fastUUID = IdUtils.fastUUID();
        String enPasswd = DigestUtils.md5DigestAsHex((registerBody.getPassword().trim() + fastUUID).getBytes());
        Member user = new Member();
        user.setUserName(registerBody.getUsername());
        user.setPassword(enPasswd);
        user.setSalt(fastUUID);
        user.setNickName(IdUtils.shortUUID());
        user.setCreateTime(LocalDateTime.now());
        boolean save = this.save(user);
        //如果保存成功，则向mq发送消息，发送内容为用户的id
        if (save) {
            String msg = user.getUserId().toString();
            rabbitTemplate.convertAndSend(BEHAVE_EXCHANGE, CREATE_ROUTING_KEY, msg);
            // 创建用户详情表member_info
            MemberInfo memberInfo = new MemberInfo();
            memberInfo.setUserId(user.getUserId());
            memberInfoService.save(memberInfo);
            return save;
        } else {
            throw new CustomException(null);
        }
    }

    private boolean userNameExist(String username) {
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getUserName, username);
        return count(queryWrapper) > 0;
    }

    private boolean telephoneExist(String telephone) {
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getTelephone, telephone);
        return count(queryWrapper) > 0;
    }

    /**
     * 用户注册-sms
     *
     * @param smsRegisterDTO
     */
    @Override
    public boolean smsRegister(SmsRegisterDTO smsRegisterDTO) {
        //判断Telephone是否存在
        if (userNameExist(smsRegisterDTO.getTelephone())) {
            throw new CustomException(PHONENUMBER_EXIST);
        }
        if (!smsRegisterDTO.getPassword().equals(smsRegisterDTO.getConfirmPassword())) {
            throw new CustomException(CONFIRM_PASSWORD_NOT_MATCH);
        }
        // 生成随机盐加密密码
        String fastUUID = IdUtils.fastUUID();
        String enPasswd = DigestUtils.md5DigestAsHex((smsRegisterDTO.getPassword().trim() + fastUUID).getBytes());
        Member user = new Member();
        user.setUserName(smsRegisterDTO.getTelephone());
        user.setTelephone(smsRegisterDTO.getTelephone());
        user.setPassword(enPasswd);
        user.setSalt(fastUUID);
        user.setNickName(IdUtils.shortUUID());
        user.setCreateTime(LocalDateTime.now());
        boolean save = this.save(user);
        //如果保存成功，则向mq发送消息，发送内容为用户的id
        if (save) {
            String msg = user.getUserId().toString();
            rabbitTemplate.convertAndSend(BEHAVE_EXCHANGE, CREATE_ROUTING_KEY, msg);
            // 创建用户详情表member_info
            MemberInfo memberInfo = new MemberInfo();
            memberInfo.setUserId(user.getUserId());
            memberInfoService.save(memberInfo);
            return save;
        } else {
            throw new CustomException(null);
        }
    }

    /**
     * 敏感词检测
     */
    private boolean sensitiveCheck(String str) {
        LambdaQueryWrapper<UserSensitive> userSensitiveLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userSensitiveLambdaQueryWrapper.select(UserSensitive::getSensitives);
        List<String> userSensitives = userSensitiveService.list(userSensitiveLambdaQueryWrapper).stream().map(UserSensitive::getSensitives).collect(Collectors.toList());
        SensitiveWordUtil.initMap(userSensitives);
        //是否包含敏感词
        Map<String, Integer> map = SensitiveWordUtil.matchWords(str);
        // 存在敏感词
        return map.size() > 0;
    }

    @Override
    public Member updateUserInfo(Member user) {
        Long userId = UserContext.getUser().getUserId();
        if (StringUtils.isNull(userId)) {
            throw new CustomException(HttpCodeEnum.NEED_LOGIN);
        }
        // 先删除缓存
        redisService.deleteObject(UserCacheConstants.USER_INFO_PREFIX + userId);
        // 敏感词过滤
        if (sensitiveCheck(user.toString())) {
            throw new CustomException(HttpCodeEnum.SENSITIVEWORD_ERROR);
        }
        user.setUserId(userId);
        user.setUpdateTime(LocalDateTime.now());
        boolean update = this.updateById(user);
        if (update) {
            return user;
        } else {
            return new Member();
        }
    }

    @Override
    public boolean updatePass(UpdatePasswordDTO dto) {
        Long userId = UserContext.getUser().getUserId();
        if (StringUtils.isNull(userId)) {
            throw new CustomException(HttpCodeEnum.NEED_LOGIN);
        }
        // 获取用户，拿到原密码加盐比较
        Member user = this.getById(userId);
        String dtoEnPasswd = DigestUtils.md5DigestAsHex((dto.getOldPassword().trim() + user.getSalt()).getBytes());
        if (!dtoEnPasswd.equals(user.getPassword())) {
            throw new CustomException(PASSWORD_ERROR);
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new CustomException(CONFIRM_PASSWORD_NOT_MATCH);
        }
        Member updateUser = new Member();
        updateUser.setUserId(userId);
        updateUser.setPassword(DigestUtils.md5DigestAsHex((dto.getConfirmPassword().trim() + user.getSalt()).getBytes()));
        return this.updateById(updateUser);
    }

    /**
     * 获取头像
     *
     * @param userId
     */
    @Override
    public String getAvatarById(Long userId) {
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Member::getAvatar);
        queryWrapper.eq(Member::getUserId, userId);
        return this.getOne(queryWrapper).getAvatar();
    }

    @Override
    public Member getMemberById(Long userId) {
        return memberRedisBatchCache.get(userId);
    }
}
