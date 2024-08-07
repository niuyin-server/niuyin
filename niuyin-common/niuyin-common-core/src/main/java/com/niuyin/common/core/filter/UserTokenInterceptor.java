package com.niuyin.common.core.filter;

import com.niuyin.common.core.context.UserContext;
import com.niuyin.model.member.domain.Member;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserTokenInterceptor implements HandlerInterceptor {

    /**
     * 得到header中的用户信息，并且存入到当前线程中
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        if (userId != null) {
            //存入到当前线程中
            Member user = new Member();
            user.setUserId(Long.valueOf(userId));
            UserContext.setUser(user);
        }
        return true;
    }

    /**
     * 清理线程中的数据
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserContext.clear();
    }
}
