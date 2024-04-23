package com.niuyin.service.notice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ws心跳检测消息枚举
 *
 * @AUTHOR: roydon
 * @DATE: 2024/3/8
 **/
@Getter
@AllArgsConstructor
public enum HeartCheckMsgEnums {

    PING("0", "ping"),
    PONG("1", "PONG"),
    ;
    private final String code;
    private final String info;

    public static HeartCheckMsgEnums findByCode(String code) {
        for (HeartCheckMsgEnums value : HeartCheckMsgEnums.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
