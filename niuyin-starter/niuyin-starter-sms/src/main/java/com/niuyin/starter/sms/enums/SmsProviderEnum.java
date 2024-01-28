package com.niuyin.starter.sms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @USER: roydon
 * @DATE: 2023/5/24 14:39
 * @Description sms短信服务供应商
 **/
@Getter
@AllArgsConstructor
public enum SmsProviderEnum {

    ALIYUN_SMS("1", "阿里云sms"),
    TENCENTYUN_SMS("2", "腾讯云sms"),
    ;

    private final String code;
    private final String info;

    public static SmsProviderEnum findByCode(String code) {
        for (SmsProviderEnum value : SmsProviderEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
