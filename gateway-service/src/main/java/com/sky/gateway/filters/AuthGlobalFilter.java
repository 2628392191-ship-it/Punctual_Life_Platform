package com.sky.gateway.filters;


import cn.hutool.core.collection.CollUtil;

import com.sky.gateway.config.AuthProperties;
import com.sky.gateway.config.JwtProperties;
import com.sky.gateway.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({AuthProperties.class,JwtProperties.class})
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtProperties jwtProperties;

    private final AuthProperties authProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取Request
        ServerHttpRequest request = exchange.getRequest();
        // 2.判断是否不需要拦截
        if(isExclude(request.getPath().toString())){
            return chain.filter(exchange);
        }
        // 3.判断是用户端请求还是管理端请求
        String path = request.getPath().toString();
        boolean isUserPath = path.startsWith("/user/");

        String token = null;
        List<String> headers;
        if (isUserPath) {
            headers = request.getHeaders().get(jwtProperties.getUserTokenName());
        } else {
            headers = request.getHeaders().get(jwtProperties.getAdminTokenName());
        }
        if (!CollUtil.isEmpty(headers)) {
            token = headers.get(0);
        }
        // 4.校验并解析token
        Long userId = null;
        try {
            String secretKey = isUserPath ? jwtProperties.getUserSecretKey() : jwtProperties.getAdminSecretKey();
            Claims claims = JwtUtil.parseJWT(secretKey, token);
            String claimKey = isUserPath ? "userId" : "empId";
            userId = Long.valueOf(claims.get(claimKey).toString());
        } catch (Throwable e) {
            log.warn("JWT解析失败 - path: {}, isUserPath: {}, error: {}", path, isUserPath, e.getMessage());
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }
        // 5.传递用户信息
        String userInfo = userId.toString();
        String headerName = isUserPath ? "user-info" : "admin-info";
        ServerWebExchange ex = exchange.mutate()
                .request(b -> b.header(headerName, userInfo))
                .build();
        ex.getAttributes().putAll(exchange.getAttributes());
        // 6.放行
        return chain.filter(ex);
    }

    private boolean isExclude(String antPath) {
        for (String pathPattern : authProperties.getExcludePaths()) {
            if(antPathMatcher.match(pathPattern, antPath)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}