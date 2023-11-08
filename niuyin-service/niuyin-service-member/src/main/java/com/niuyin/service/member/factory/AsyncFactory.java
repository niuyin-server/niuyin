package com.niuyin.service.member.factory;

import com.niuyin.common.utils.IpUtils;
import com.niuyin.common.utils.ServletUtils;
import com.niuyin.common.utils.spring.SpringUtils;
import com.niuyin.model.member.domain.Member;
import com.niuyin.service.member.service.IMemberService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.TimerTask;

/**
 * 异步工厂,产生任务
 */
@Slf4j
public class AsyncFactory {

    /**
     * 记录登录用户信息
     *
     * @param userId
     */
    public static TimerTask recordLoginUserInfo(Long userId) {
        return new TimerTask() {
            @Override
            public void run() {
                Member user = new Member();
                user.setUserId(userId);
                user.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
                user.setLoginDate(LocalDateTime.now());
                SpringUtils.getBean(IMemberService.class).updateById(user);
            }
        };
    }

}
