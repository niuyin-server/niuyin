package com.niuyin.starter.sms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @USER: roydon
 * @DATE: 2024/1/28
 * @Description sms短信模板类型
 * 1:登录验证码 2:注册验证码 3:身份验证 4:登录异常 5:修改密码 6:信息变更 7:短信验证码
 **/
@Getter
@AllArgsConstructor
public enum AliyunSmsTemplateType {

    AUTH_CODE("1", "验证码"),
    MESSAGE("2", "短信"),
    /**
     * 身份验证
     */
    SMS_000000001("1", ""),
    /**
     * 登录确认
     */
    SMS_000000002("2", ""),
    /**
     * 登录异常
     */
    SMS_000000003("3", ""),
    /**
     * 用户注册
     */
    SMS_000000004("4", ""),
    /**
     * 修改密码
     */
    SMS_000000005("5", ""),
    /**
     * 信息变更
     */
    SMS_000000006("6", ""),
    /**
     * 阿里云赠送
     * {您的验证码为：${code}，请勿泄露于他人！}
     */
    SMS_254150267("7", ""),
    /**
     * 登录短信验证码 true 仅数字
     * {您的验证码为 ${code} ，该验证码1分钟内有效，请勿泄露于他人。}
     */
    SMS_460955023("8", "");;

    private final String code;
    private final String info;

    public static AliyunSmsTemplateType findByCode(String code) {
        for (AliyunSmsTemplateType value : AliyunSmsTemplateType.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
