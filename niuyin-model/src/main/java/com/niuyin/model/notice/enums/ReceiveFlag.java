package com.niuyin.model.notice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ReceiveFlag
 * 消息接收状态；0未读1已读
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/16
 **/
@Getter
@AllArgsConstructor
public enum ReceiveFlag {

    WAIT("0", "未读"),
    RECEIVE("1", "已读"),
    ;
    private final String code;
    private final String info;

    public static ReceiveFlag findByCode(String code) {
        for (ReceiveFlag value : ReceiveFlag.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

}
