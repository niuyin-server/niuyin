package com.niuyin.model.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录选项
 *
 * @AUTHOR: roydon
 **/
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    UP("0", "账号密码登录", "upLoginStrategy"),
    SMS("1", "手机验证码登录", "smsLoginStrategy"),
    ;

    private final String code;
    private final String info;
    private final String strategy;

    public static ShowStatusEnum findByCode(String code) {
        for (ShowStatusEnum value : ShowStatusEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
