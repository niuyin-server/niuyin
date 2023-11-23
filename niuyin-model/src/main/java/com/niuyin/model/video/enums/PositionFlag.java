package com.niuyin.model.video.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PositionFlag
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/21
 * 定位功能0关闭1开启
 **/
@Getter
@AllArgsConstructor
public enum PositionFlag {

    DISABLE("0", "禁用定位"),
    OPEN("1", "开启定位"),
    ;

    private final String code;
    private final String info;

    public static PositionFlag findByCode(String code) {
        for (PositionFlag value : PositionFlag.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
