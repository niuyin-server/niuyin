package com.niuyin.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.niuyin.gateway.constant.TokenConstants;
import com.niuyin.gateway.util.JwtUtil;
import com.niuyin.gateway.util.R;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Order(-100) //优先级设置  值越小  优先级越高
@Component
public class AuthorizeFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //2.放行接口
        if (request.getURI().getPath().contains("/login")
                || request.getURI().getPath().contains("/sms-login")
                || request.getURI().getPath().contains("/register")
                || request.getURI().getPath().contains("/swagger-ui")
                || request.getURI().getPath().contains("/api/v1/app/recommend")
        ) {
            // 请求头 userId制空
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("userId", "0");
            }).build();
            //重置请求
            exchange.mutate().request(serverHttpRequest);
            return chain.filter(exchange);//放行
        }

        //3.获取token
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        //4.判断token是否存在
        if (StringUtils.isBlank(token)) {
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8");
            R<?> result = R.fail(401, "用户未登录");
            DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(result).getBytes());
            return response.writeWith(Mono.just(dataBuffer));
        }

        //5.判断token是否有效
        try {
            String strToken = token.split(TokenConstants.PREFIX)[1];
            Claims claimsBody = JwtUtil.getClaimsBody(strToken);
            //是否是过期
            int result = JwtUtil.verifyToken(claimsBody);
            if (result == 1 || result == 2) {
                response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8");
                DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(R.fail(401, "用户未登录")).getBytes());
                return response.writeWith(Mono.just(dataBuffer));
            }
            //获取用户信息
            Object userId = claimsBody.get("id");

            //存储header中
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("userId", userId + "");
            }).build();
            //重置请求
            exchange.mutate().request(serverHttpRequest);

        } catch (Exception e) {
            e.printStackTrace();
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8");
            DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(R.fail(401, "用户未登录")).getBytes());
            return response.writeWith(Mono.just(dataBuffer));
        }
        return chain.filter(exchange);
    }
}
