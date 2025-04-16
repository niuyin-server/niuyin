//package com.niuyin.gateway.handler;
//
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SseGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
//
//    @Override
//    public GatewayFilter apply(Object config) {
//        return (exchange, chain) -> {
//            // 禁用响应缓冲
//            exchange.getResponse().headers().set("X-Accel-Buffering", "no");
//            exchange.getResponse().headers().set("Cache-Control", "no-cache");
//            return chain.filter(exchange);
//        };
//    }
//}
