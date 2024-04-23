package com.niuyin.service.notice.controller.v1;

import com.alibaba.fastjson2.JSON;
import com.niuyin.model.notice.vo.WebSocketBaseResp;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {

    // 用来保存在线连接数
    private static final Map<Long, Session> sessionPool = new ConcurrentHashMap<>();

    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") Long userId) {
        try {
            session.setMaxIdleTimeout(30000L);
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
    public static <T> void sendOneMessage(Long userId, WebSocketBaseResp<T> message) {
        Session session = sessionPool.get(userId);
        if (session != null && session.isOpen()) {
            String jsonString = JSON.toJSONString(message);
            log.info("websocket: 单点消息:" + jsonString);
            session.getAsyncRemote().sendText(jsonString);
        }
    }

    /**
     * 连接是否存在
     *
     * @param userId
     * @return boolean
     */
    public static boolean isConnected(Long userId) {
        return sessionPool.containsKey(userId);
    }

    /**
     * 心跳检测
     *
     * @param ping
     * @return
     */
    public static synchronized int sendPing(String ping) {
        if (sessionPool.isEmpty()) {
            return 0;
        }
        AtomicInteger count = new AtomicInteger(0);
        sessionPool.forEach((userId, session) -> {
            count.getAndIncrement();
            try {
                session.getAsyncRemote().sendText(ping);
            } catch (Exception e) {
                sessionPool.remove(userId);
                log.info("客户端心跳检测异常移除: " + userId + "，心跳发送失败，已移除！");
            }
        });
        return sessionPool.size();
    }


}
