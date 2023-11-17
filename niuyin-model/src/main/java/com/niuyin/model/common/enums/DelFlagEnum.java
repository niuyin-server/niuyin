package com.niuyin.model.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DelFlagEnum
 * 通用删除字段枚举
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/17
 **/
@Getter
@AllArgsConstructor
public enum DelFlagEnum {

    EXIST("0", "存在"),
    DISABLE("1", "禁用"),
    DELETED("2", "删除"),
    ;

    private final String code;
    private final String info;

    public static DelFlagEnum findByCode(String code) {
        for (DelFlagEnum value : DelFlagEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
