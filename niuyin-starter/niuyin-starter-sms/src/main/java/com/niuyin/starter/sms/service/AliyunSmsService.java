package com.niuyin.starter.sms.service;

import com.niuyin.starter.sms.domain.model.AliyunSmsResponse;
import com.niuyin.starter.sms.enums.AliyunSmsTemplateType;

/**
 * AliyunSmsService
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/28
 **/
public interface AliyunSmsService {

    /**
     * 发送验证码
     *
     * @param phone 电话号码
     * @param type 短信模板
     * @param code 随机数字二维码 6位
     * @return code
     */
    AliyunSmsResponse sendAuthCode(String phone, AliyunSmsTemplateType type, String code);

}
