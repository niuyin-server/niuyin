package com.niuyin.common.core.config;

import com.niuyin.common.core.context.UserContext;
import com.niuyin.model.member.domain.Member;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebFilter("/*")
public class UserInfoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        try {
            // 从请求头获取用户ID
            String userId = httpRequest.getHeader("X-User-Id");
            log.debug("登录用户id: " + userId);
            // 存入 ThreadLocal
            if (userId != null) {
                Member user = new Member();
                user.setUserId(Long.valueOf(userId));
                UserContext.setUser(user);
            }
            chain.doFilter(request, response);
        } finally {
            // 清除 ThreadLocal 防止内存泄漏
            UserContext.clear();
        }
    }
}
