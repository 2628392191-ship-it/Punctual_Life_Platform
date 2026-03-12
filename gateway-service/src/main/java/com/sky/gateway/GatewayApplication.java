package com.sky.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class GatewayApplication {
    public static void main(String[] args) {
        log.info("网关启动成功");
        SpringApplication.run(GatewayApplication.class, args);
    }
}
