package com.niuyin.gateway.resolver;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * VideoKeyResolver
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/30
 **/
@Component
public class VideoKeyResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        return Mono.just(hostAddress);
    }
}
