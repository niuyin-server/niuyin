package com.niuyin.starter.sms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @USER: roydon
 * @DATE: 2023/5/24 14:39
 * @Description sms短信发送类型
 * 1:登录验证码 2:注册验证码 3:身份验证 4:登录异常 5:修改密码 6:信息变更 7:消息通知
 **/
@Getter
@AllArgsConstructor
public enum SmsSendType {

    AUTH_CODE_LOGIN("1", "登录验证码"),
    AUTH_CODE_REGISTER("2", "注册验证码"),
    AUTH_ID("3", "身份验证"),
    ERROR_LOGIN("4", "登录异常"),
    EDIT_PSD("5", "修改密码"),
    PROFILE_UPDATE("6", "信息变更"),
    ;

    private final String code;
    private final String info;

    public static SmsSendType findByCode(String code) {
        for (SmsSendType value : SmsSendType.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

}
