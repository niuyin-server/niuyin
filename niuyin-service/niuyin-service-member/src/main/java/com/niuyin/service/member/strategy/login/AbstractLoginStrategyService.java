package com.niuyin.service.member.strategy.login;

import com.niuyin.common.core.utils.JwtUtil;
import com.niuyin.service.member.strategy.LoginStrategy;

import javax.annotation.PostConstruct;

/**
 * AbstractLoginStrategyService
 *
 * @AUTHOR: roydon
 * @DATE: 2024/3/9
 **/
public abstract class AbstractLoginStrategyService<T> implements LoginStrategy<T> {

    @PostConstruct
    protected void registerStrategyToFactory() {
        LoginStrategyFactory.registerLoginStrategyMap(getStrategyName(), this);
    }

    /**
     * 策略名称
     */
    protected abstract String getStrategyName();

    /**
     * 校验用户
     *
     * @return userId
     */
    protected abstract Long verifyCredentials(T loginDto);

    /**
     * 记录登录信息
     */
    protected abstract void recordLoginUserInfo(Long userId);

    /**
     * 登录方法
     *
     * @param loginDto
     * @return
     */
    @Override
    public String login(T loginDto) {
        Long userId = verifyCredentials(loginDto);
        recordLoginUserInfo(userId);
        return JwtUtil.getToken(userId);
    }
}
