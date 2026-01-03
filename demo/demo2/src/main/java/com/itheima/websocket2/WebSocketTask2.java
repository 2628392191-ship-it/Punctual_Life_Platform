package com.itheima.websocket2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class WebSocketTask2 {

      @Autowired
      private WebSocketServer2 webSocketServer2;


      @Scheduled(cron = "0/5 * * * * ?")
      public void sendMessageToClient() {
      //通过注入拿到的session集合是单例的，是与在WebSocketServer2中创建的session集合不是同一个对象（若集合没有static标识符）
      //这里的方法调用是Spring单例模式创建的对象的方法调用
      //每次调用若集合没有static标识符，则每次调用都是创建新的对象从而集合大小为0，若集合有static标识符，则每次调用都是调用同一个集合对象
      webSocketServer2.sendtoAll("服务器返回："+new Date());
    }

}
