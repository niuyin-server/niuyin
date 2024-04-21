package com.niuyin.service.member.constants;

/**
 * SmsConstant
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/28
 **/
public class SmsConstant {

    /**
     *
     */
    public static final String MESSAGE_OK = "OK";

    /**
     * 用户登录短信验证码key
     */
    public static final String SMS_LOGIN_AUTH_CODE_KEY = "sms:login_captcha:";

    // 登录验证码过期时间 单位：分钟
    public static final long SMS_LOGIN_AUTH_CODE_EXPIRE_TIME = 1L;

    /**
     * 用户注册短信验证码key
     */
    public static final String SMS_REGISTER_CODE_KEY = "sms:register_captcha:";
    // 注册验证码过期时间 单位：分钟
    public static final long SMS_REGISTER_AUTH_CODE_EXPIRE_TIME = 5L;

}
