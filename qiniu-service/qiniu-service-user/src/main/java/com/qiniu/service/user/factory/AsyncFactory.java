package com.qiniu.service.user.factory;

import com.qiniu.common.utils.IpUtils;
import com.qiniu.common.utils.ServletUtils;
import com.qiniu.common.utils.spring.SpringUtils;
import com.qiniu.model.user.domain.User;
import com.qiniu.service.user.service.IUserService;
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
                User user = new User();
                user.setUserId(userId);
                user.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
                user.setLoginDate(LocalDateTime.now());
                SpringUtils.getBean(IUserService.class).updateById(user);
            }
        };
    }

}
