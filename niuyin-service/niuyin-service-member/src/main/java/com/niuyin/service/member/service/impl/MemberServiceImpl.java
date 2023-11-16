package com.niuyin.service.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.utils.IdUtils;
import com.niuyin.common.utils.ServletUtils;
import com.niuyin.common.utils.audit.SensitiveWordUtil;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.IpUtils;
import com.niuyin.common.utils.JwtUtil;
import com.niuyin.feign.social.RemoteSocialService;
import com.niuyin.feign.video.RemoteVideoService;
import com.niuyin.model.common.enums.HttpCodeEnum;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.model.member.domain.UserSensitive;
import com.niuyin.model.member.dto.LoginUserDTO;
import com.niuyin.model.member.dto.RegisterBody;
import com.niuyin.model.member.dto.UpdatePasswordDTO;
import com.niuyin.service.member.constants.UserCacheConstants;
import com.niuyin.service.member.mapper.MemberMapper;
import com.niuyin.service.member.service.IMemberInfoService;
import com.niuyin.service.member.service.IUserSensitiveService;
import com.niuyin.service.member.service.IMemberService;
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

}
