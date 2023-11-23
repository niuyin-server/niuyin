package com.niuyin.model.video.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ShowType
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/21
 * 展示类型（0全部可见1好友可见2自己可见）
 **/
@Getter
@AllArgsConstructor
public enum ShowType {

    ALL("0", "全部可见"),
    FRIEND("1", "好友可见"),
    MYSELF("2", "自己可见"),
    ;

    private final String code;
    private final String info;

    public static ShowType findByCode(String code) {
        for (ShowType value : ShowType.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
