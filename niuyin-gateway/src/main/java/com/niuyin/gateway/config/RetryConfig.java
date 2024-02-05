package com.niuyin.gateway.config;

import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 重试过滤器
 */
@Configuration
public class RetryConfig {

    @Bean
    public RetryGatewayFilterFactory retryGatewayFilterFactory() {
        return new RetryGatewayFilterFactory();
    }
}
