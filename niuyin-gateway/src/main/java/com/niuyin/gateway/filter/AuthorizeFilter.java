package com.niuyin.gateway.filter;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.niuyin.gateway.constant.TokenConstants;
import com.niuyin.gateway.util.JwtUtil;
import com.niuyin.gateway.util.R;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-100) //优先级设置  值越小  优先级越高
@Component
public class AuthorizeFilter implements GlobalFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //2.放行接口
        if (request.getURI().getPath().contains("/login")
                || request.getURI().getPath().contains("/sms-login")
                || request.getURI().getPath().contains("/register")
                || request.getURI().getPath().contains("/app/sms-register")
                || request.getURI().getPath().contains("/swagger-ui")
                || request.getURI().getPath().contains("/api/v1/feed")
                || request.getURI().getPath().contains("/api/v1/pushVideo")
                || request.getURI().getPath().contains("/api/v1/app/recommend")
                || request.getURI().getPath().contains("/api/v1/app/hotVideo")
                || request.getURI().getPath().contains("/api/v1/app/video/hotSearch")
                || request.getURI().getPath().contains("/api/v1/hot")
                || request.getURI().getPath().contains("/api/v1/video/search/hot")
                || request.getURI().getPath().contains("/websocket")
                || request.getURI().getPath().contains("/userVideoBehave/syncViewBehave")
                || request.getURI().getPath().contains("/api/v1/video/feed")
                || request.getURI().getPath().contains("/api/v1/category/tree")
                || request.getURI().getPath().contains("/api/v1/category/parentList")
                || request.getURI().getPath().contains("/api/v1/category/children")
                || request.getURI().getPath().contains("/api/v1/category/pushVideo")
                || request.getURI().getPath().contains("/chat/stream")
                || request.getURI().getPath().contains("/test")
        ) {
            //若存在token获取token解析token
            String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isNotBlank(token)) {
                String strToken = token.split(TokenConstants.PREFIX)[1];
                Claims claimsBody = JwtUtil.getClaimsBody(strToken);
                //是否是过期
                int result = JwtUtil.verifyToken(claimsBody);
                if (result == 1 || result == 2) {
                    // 令牌过期

                } else {
                    //获取用户信息
                    Object userId = claimsBody.get("id");

                    //存储header中
                    ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> httpHeaders.add("userId", userId + "")).build();
                    //重置请求
                    exchange.mutate().request(serverHttpRequest);
                    return chain.filter(exchange);
                }
            }
            // 请求头 userId 置空
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> httpHeaders.add("userId", "0")).build();
            //重置请求
            exchange.mutate().request(serverHttpRequest);
            return chain.filter(exchange);//放行
        }

        // 定义不需要拦截的路径列表
//        String[] excludePatterns = {
//                "/login",
//                "/sms-login",
//                "/register",
//                "/app/sms-register",
//                "/swagger-ui/**",
//                "/api/v1/feed",
//                "/api/v1/pushVideo",
//                "/api/v1/app/recommend",
//                "/api/v1/app/hotVideo",
//                "/api/v1/app/video/hotSearch",
//                "/api/v1/hot",
//                "/api/v1/video/search/hot",
//                "/websocket",
//                "/userVideoBehave/syncViewBehave",
//                "/api/v1/video/feed",
//                "/api/v1/category/tree",
//                "/api/v1/category/parentList",
//                "/api/v1/category/children",
//                "/api/v1/category/pushVideo",
//                "/auth/**"
//        };
//
//        for (String pattern : excludePatterns) {
//            if (PATH_MATCHER.match(pattern, request.getURI().getPath())) {
//                // 表示该路径被排除，不应被拦截
//            }
//        }

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
                DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(R.fail(401, "令牌失效，请重新登录")).getBytes());
                return response.writeWith(Mono.just(dataBuffer));
            }
            //获取用户信息
            Object userId = claimsBody.get("id");

            //存储header中
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> httpHeaders.add("userId", userId + "")).build();
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
