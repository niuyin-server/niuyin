//package com.niuyin.gateway.handler;
//
//import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//public class ResponseBodyRewrite implements ModifyResponseBodyGatewayFilterFactory.RewriteFunction<String, String> {
//    @Override
//    public Mono<String> apply(ServerWebExchange exchange, String body) {
//        // 对于SSE流，直接传递不做修改
//        return Mono.just(body);
//    }
//}
