package com.niuyin.service.member.strategy;

/**
 * 第三方策略模式
 */
public interface LoginStrategy<T> {

    /**
     * 登录方法
     *
     * @param loginDto
     * @return
     */
    String login(T loginDto);

}
