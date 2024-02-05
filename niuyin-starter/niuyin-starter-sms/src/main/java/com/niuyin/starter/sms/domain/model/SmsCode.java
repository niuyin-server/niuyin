package com.niuyin.starter.sms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短信验证码返回体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsCode {

    private String code;
    private Long expireTime;

}
