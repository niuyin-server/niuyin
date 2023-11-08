package com.qiniu.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.IdUtils;
import com.qiniu.common.utils.IpUtils;
import com.qiniu.common.utils.JwtUtil;
import com.qiniu.common.utils.ServletUtils;
import com.qiniu.common.utils.audit.SensitiveWordUtil;
import com.qiniu.common.utils.date.DateUtils;
import com.qiniu.common.utils.executor.AsyncExecutor;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.model.common.enums.HttpCodeEnum;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.domain.UserSensitive;
import com.qiniu.model.user.dto.LoginUserDTO;
import com.qiniu.model.user.dto.RegisterBody;
import com.qiniu.model.user.dto.UpdatePasswordDTO;
import com.qiniu.service.user.constants.UserCacheConstants;
import com.qiniu.service.user.factory.AsyncFactory;
import com.qiniu.service.user.mapper.UserMapper;
import com.qiniu.service.user.service.IUserSensitiveService;
import com.qiniu.service.user.service.IUserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.qiniu.model.common.enums.HttpCodeEnum.*;

/**
 * 用户表(User)表服务实现类
 *
 * @author roydon
 * @since 2023-10-24 19:18:26
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private IUserSensitiveService userSensitiveService;

    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    @Override
    public User queryById(Long userId) {
        return this.getById(userId);
    }

    @Override
    public String login(LoginUserDTO loginUserDTO) {
        //1.检查参数
        if (StringUtils.isBlank(loginUserDTO.getUsername()) || StringUtils.isBlank(loginUserDTO.getPassword())) {
            throw new CustomException(SYSTEM_ERROR);
        }
        //2.查询用户
        User dbUser = getOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, loginUserDTO.getUsername()));
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
            throw new CustomException(USER_NOT_EXISTS);
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    @Async
    public void recordLoginUserInfo(Long userId) {
        User user = new User();
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
        User user = new User();
        user.setUserName(registerBody.getUsername());
        user.setPassword(enPasswd);
        user.setSalt(fastUUID);
        user.setNickName(IdUtils.shortUUID());
        user.setCreateTime(LocalDateTime.now());
        return this.save(user);
    }

    private boolean userNameExist(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username);
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
    public User updateUserInfo(User user) {
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
            return new User();
        }
    }

    @Override
    public boolean updatePass(UpdatePasswordDTO dto) {
        Long userId = UserContext.getUser().getUserId();
        if (StringUtils.isNull(userId)) {
            throw new CustomException(HttpCodeEnum.NEED_LOGIN);
        }
        // 获取用户，拿到原密码加盐比较
        User user = this.getById(userId);
        String dtoEnPasswd = DigestUtils.md5DigestAsHex((dto.getOldPassword().trim() + user.getSalt()).getBytes());
        if (!dtoEnPasswd.equals(user.getPassword())) {
            throw new CustomException(PASSWORD_ERROR);
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new CustomException(CONFIRM_PASSWORD_NOT_MATCH);
        }
        User updateUser = new User();
        updateUser.setUserId(userId);
        updateUser.setPassword(DigestUtils.md5DigestAsHex((dto.getConfirmPassword().trim() + user.getSalt()).getBytes()));
        return this.updateById(updateUser);
    }
}
