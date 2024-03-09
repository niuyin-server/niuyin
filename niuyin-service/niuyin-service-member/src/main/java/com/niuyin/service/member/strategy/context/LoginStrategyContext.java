package com.niuyin.service.member.strategy.context;

import com.niuyin.model.member.enums.LoginTypeEnum;
import com.niuyin.service.member.strategy.login.AbstractLoginStrategyService;
import com.niuyin.service.member.strategy.login.LoginStrategyFactory;
import org.springframework.stereotype.Service;

/**
 * 登录策略上下文
 */
@Service
public class LoginStrategyContext {

    /**
     * 登录策略
     */
    public <T> String executeLoginStrategy(T loginDto, LoginTypeEnum loginTypeEnum) {
        AbstractLoginStrategyService<T> loginByName = LoginStrategyFactory.getLoginService(loginTypeEnum.getStrategy());
        return loginByName.login(loginDto);
    }

}
