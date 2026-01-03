package com.itheima.websocket2;


import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ServerEndpoint("/webSocketServer2")
@Component
public class WebSocketServer2 {
    //WebSocketServer（不同的实例类似于多例）创建的实例与Spring注入（单例）的实例是不同的对象
    //而通过static来将WebSocketServer创建的实例放入其中，通过方法调用到静态变量中，所以这里拿到的实例是同一个对象
    private static List<Session> sessionList=new ArrayList<>();

    @OnOpen
    //拿到从客户端拿到的消息保存到session的信息来给客户端返回消息
    //每一次连接成功，就有会话生成而session的生存周期是到会话结束而中断的
    public void onOpen(Session session) {
        System.out.println("连接成功");
        sessionList.add(session);
    }

    //有消息来的时候，调用此方法
    @OnMessage
    public void onMessage(Session session,String message) throws IOException {
        System.out.println("收到客户端消息：" + message);
    }

    @OnClose
    public void onClose() {
        System.out.println("连接关闭");
    }

   //这里就模拟了对每个会话发送消息类似于广播
   public void sendtoAll(String message){
       System.out.println(sessionList.size());
        sessionList.forEach(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
   }


}
