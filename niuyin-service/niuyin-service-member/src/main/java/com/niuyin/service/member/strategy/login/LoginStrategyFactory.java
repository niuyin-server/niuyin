package com.niuyin.service.member.strategy.login;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录策略工厂
 *
 * @AUTHOR: roydon
 * @DATE: 2024/3/9
 **/
public class LoginStrategyFactory {

    /**
     * 登录策略集合
     */
    static Map<String, AbstractLoginStrategyService<?>> loginStrategyMap = new ConcurrentHashMap<>(2);

    /**
     * 将策略类放入工厂
     *
     * @param strategyName                 策略名称
     * @param abstractLoginStrategyService 策略类
     */
    public static <T> void registerLoginStrategyMap(String strategyName, AbstractLoginStrategyService<T> abstractLoginStrategyService) {
        loginStrategyMap.put(strategyName, abstractLoginStrategyService);
    }

    /**
     * 根据名称获取策略类
     *
     * @param strategyName 策略名称
     * @return 对应的策略类
     */
    @SuppressWarnings("unchecked")
    public static <K> AbstractLoginStrategyService<K> getLoginService(String strategyName) {
        return (AbstractLoginStrategyService<K>) loginStrategyMap.get(strategyName);
    }

    /**
     * 构造器私有
     */
    private LoginStrategyFactory() {

    }

}
