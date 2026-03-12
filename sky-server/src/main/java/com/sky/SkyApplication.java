package com.sky;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//可以在启动类上加@MapperScan("com.sky.setmeal.mapper")表示注入哪些mapper接口
@SpringBootApplication
@EnableScheduling //开启SpringTask定时任务功能
@EnableTransactionManagement //开启注解方式的事务管理
@EnableCaching //开启SpringCache缓存功能
@Slf4j
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}
