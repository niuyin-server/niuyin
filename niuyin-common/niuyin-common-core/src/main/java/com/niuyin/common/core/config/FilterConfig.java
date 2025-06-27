package com.niuyin.common.core.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<UserInfoFilter> userInfoFilter() {
        FilterRegistrationBean<UserInfoFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserInfoFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // 最高优先级
        return registration;
    }

}
