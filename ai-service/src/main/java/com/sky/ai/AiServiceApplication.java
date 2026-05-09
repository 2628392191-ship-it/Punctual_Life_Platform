package com.sky.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients("com.sky.api.client")
@SpringBootApplication
@Slf4j
public class AiServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiServiceApplication.class, args);
        log.info("AI经营分析服务启动成功");
    }
}
