package com.sky.report;

import org.mybatis.spring.annotation.MapperScan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients("com.sky.api.client")
@SpringBootApplication
@MapperScan("com.sky.report.mapper")
@Slf4j
public class ReportServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportServiceApplication.class, args);
        log.info("报表服务启动成功");
    }
}
