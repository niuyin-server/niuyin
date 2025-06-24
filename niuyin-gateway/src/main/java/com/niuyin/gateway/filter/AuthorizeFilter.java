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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Order(-100) // 优先级设置 值越小 优先级越高
@Component
public class AuthorizeFilter implements GlobalFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // 定义不需要拦截的路径列表
    private static final String[] EXCLUDE_PATHS = {
            "/*/api/v1/login", // 登录接口：/member/api/v1/login
            "/sms-login",
            "/register",
            "/app/sms-register",
            "/swagger-ui/**",
            "/api/v1/feed",
            "/api/v1/pushVideo",
            "/api/v1/app/recommend",
            "/api/v1/app/hotVideo",
            "/api/v1/app/video/hotSearch",
            "/api/v1/hot",
            "/api/v1/video/search/hot",
            "/websocket",
            "/userVideoBehave/syncViewBehave",
            "/api/v1/video/feed",
            "/api/v1/category/tree",
            "/api/v1/category/parentList",
            "/api/v1/category/children",
            "/api/v1/category/pushVideo",
            "/chat/stream",
            "/test"
    };

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 路径放行检查
        if (isExcludePath(request.getURI().getPath())) {
            return handleTokenAndProceed(exchange, chain, request, response);
        }

        // 获取并验证 Token
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(token)) {
            return writeUnauthorized(response, "用户未登录");
        }

        try {
            String strToken = token.split(TokenConstants.PREFIX)[1];
            Claims claimsBody = JwtUtil.getClaimsBody(strToken);

            int verifyResult = JwtUtil.verifyToken(claimsBody);
            if (verifyResult == 1 || verifyResult == 2) {
                return writeUnauthorized(response, "令牌失效，请重新登录");
            }

            // 提取用户信息并设置到请求头
            Object userId = claimsBody.get("id");
            log.debug("登录用户id: {}", userId);

            ServerHttpRequest newRequest = request.mutate()
                    .header("X-User-Id", userId.toString())
                    .build();

            ServerWebExchange newExchange = exchange.mutate()
                    .request(newRequest)
                    .build();

            return chain.filter(newExchange);
        } catch (Exception e) {
            log.error("Token解析失败", e);
            return writeUnauthorized(response, "用户未登录");
        }
    }

    /**
     * 处理免登录接口的token情况（可选）
     */
    private Mono<Void> handleTokenAndProceed(ServerWebExchange exchange, GatewayFilterChain chain,
                                             ServerHttpRequest request, ServerHttpResponse response) {
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        log.debug("token: {}", token);

        if (StringUtils.isNotBlank(token)) {
            try {
                String strToken = token.split(TokenConstants.PREFIX)[1];
                Claims claimsBody = JwtUtil.getClaimsBody(strToken);
                int result = JwtUtil.verifyToken(claimsBody);

                if (result != 1 && result != 2) {
                    Object userId = claimsBody.get("id");
                    log.debug("登录用户id: {}", userId);

                    ServerHttpRequest newRequest = request.mutate()
                            .header("X-User-Id", userId.toString())
                            .build();

                    ServerWebExchange newExchange = exchange.mutate()
                            .request(newRequest)
                            .build();

                    return chain.filter(newExchange);
                }
            } catch (Exception ignored) {}
        }

        // 无有效token或token过期，添加匿名用户ID
        ServerHttpRequest newRequest = request.mutate()
                .header("X-User-Id", "0")
                .build();

        ServerWebExchange newExchange = exchange.mutate()
                .request(newRequest)
                .build();

        return chain.filter(newExchange);
    }

    /**
     * 判断是否是排除路径
     */
    private boolean isExcludePath(String path) {
        for (String pattern : EXCLUDE_PATHS) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回未授权错误
     */
    private Mono<Void> writeUnauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatusCode.valueOf(401));
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");

        DataBuffer buffer = response.bufferFactory().wrap(
                JSON.toJSONString(R.fail(401, message)).getBytes(StandardCharsets.UTF_8)
        );
        return response.writeWith(Mono.just(buffer));
    }
}
