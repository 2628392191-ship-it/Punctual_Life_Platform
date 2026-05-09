package com.sky.api.config;


import com.sky.gateway.utils.common.context.BaseContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userInfoRequestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Long userId = BaseContext.getCurrentId();
                if(userId == null) {
                    return;
                }
                template.header("admin-info", userId.toString());
                template.header("user-info", userId.toString());
            }
        };
    }

}