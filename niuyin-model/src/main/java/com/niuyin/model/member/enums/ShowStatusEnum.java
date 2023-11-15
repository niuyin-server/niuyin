package com.niuyin.model.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 展示状态枚举
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/15
 **/
@Getter
@AllArgsConstructor
public enum ShowStatusEnum {

    SHOW("0", "展示"),
    HIDE("1", "隐藏"),
    ;
    private final String code;
    private final String info;

    public static ShowStatusEnum findByCode(String code) {
        for (ShowStatusEnum value : ShowStatusEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

}
