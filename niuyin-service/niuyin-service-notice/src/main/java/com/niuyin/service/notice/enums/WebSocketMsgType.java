package com.niuyin.service.notice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * WebSocket消息类型
 *
 * @AUTHOR: roydon
 * @DATE: 2024/3/8
 **/
@Getter
@AllArgsConstructor
public enum WebSocketMsgType {

    HEART_CHECK("0", "心跳包"),
    TEXT("1", "文本消息"),
    NOTICE_UNREAD_COUNT("2", "消息未读数"),
    ;

    private final String code;
    private final String info;

    public static WebSocketMsgType findByCode(String code) {
        for (WebSocketMsgType value : WebSocketMsgType.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
