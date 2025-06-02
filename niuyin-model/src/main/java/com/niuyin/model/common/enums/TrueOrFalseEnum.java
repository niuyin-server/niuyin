package com.niuyin.model.common.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TrueOrFalseEnum
 *
 * @AUTHOR: roydon
 * @DATE: 2025/6/2
 **/
@Getter
@AllArgsConstructor
public enum TrueOrFalseEnum {
    TRUE("1", "true"),
    FALSE("0", "false"),
    ;

    private final String code;
    private final String info;

    public static TrueOrFalseEnum findByCode(String code) {
        for (TrueOrFalseEnum value : TrueOrFalseEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

    public static boolean isFalse(String code) {
        return ObjUtil.equal(TRUE.code, code);
    }

    public static boolean isTrue(String code) {
        return ObjUtil.equal(FALSE.code, code);
    }
}
