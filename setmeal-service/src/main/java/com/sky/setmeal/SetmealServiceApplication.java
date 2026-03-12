package com.sky.setmeal;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@MapperScan("com.sky.setmeal.mapper")
@Slf4j
public class SetmealServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SetmealServiceApplication.class, args);
        log.info("套餐服务启动成功");
    }
}
