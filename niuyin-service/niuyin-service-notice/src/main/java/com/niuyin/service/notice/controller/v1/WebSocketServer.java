package com.niuyin.service.notice.controller.v1;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {

    // 用来存在线连接数
    private static final Map<Long, Session> sessionPool = new ConcurrentHashMap<>();

    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") Long userId) {
        try {
            sessionPool.put(userId, session);
            log.info("websocket消息: 有新的连接，总数为:" + sessionPool.size());
        } catch (Exception e) {
        }
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam(value = "userId") Long userId) {
        log.info("websocket消息: 收到客户端消息:" + message);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("userId") Long userId) {
        sessionPool.remove(userId);
        log.info("有一连接关闭，移除userId={}的用户session, 当前在线人数为：{}", userId, sessionPool.size());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("错误原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 此为单点消息
     */
    @SneakyThrows
    public static void sendOneMessage(Long userId, String message) {
        Session session = sessionPool.get(userId);
        if (session != null && session.isOpen()) {
            log.info("websocket: 单点消息:" + message);
            session.getAsyncRemote().sendText(message);
        }
    }
}
