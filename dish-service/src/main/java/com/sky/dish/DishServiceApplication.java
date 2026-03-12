package com.sky.dish;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableFeignClients("com.sky.api.client")
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.sky.dish.mapper")
@Slf4j
public class DishServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DishServiceApplication.class, args);
        log.info("菜品服务启动成功");
    }
}
