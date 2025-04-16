package com.niuyin.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.WebFilter;

@Configuration
public class SseConfig {

    @Bean
    public WebFilter sseWebFilter() {
        return (exchange, chain) -> {
            // 确保 SSE 相关头信息正确
            HttpHeaders headers = exchange.getResponse().getHeaders();
            headers.set("X-Accel-Buffering", "no");
            headers.set("Cache-Control", "no-cache");
            headers.set("Connection", "keep-alive");
            return chain.filter(exchange);
        };
    }
}
