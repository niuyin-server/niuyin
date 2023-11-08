package com.qiniu.feign.config;

import com.qiniu.feign.interceptor.UserInterceptor;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FeignConfig
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/4
 **/
@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userInterceptor() {
        return new UserInterceptor();
    }
}
