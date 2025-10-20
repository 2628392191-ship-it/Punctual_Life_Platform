package com.itheima.task;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class MyTask {

        //每间隔2秒执行一次
        @Scheduled(cron = "0/2 * * * * ?")
        public void print(){
            System.out.println("打印时间"+new Date());
        }




}
