package com.niuyin.service.member.service;

import com.niuyin.model.member.dto.SmsRegisterDTO;
import com.niuyin.starter.sms.domain.model.SmsCode;

/**
 * RegisterService
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/28
 **/
public interface RegisterService {

    /**
     * 发送注册验证码
     * @param telephone
     * @return
     */
    SmsCode sendRegisterAuthCode(String telephone);

    /**
     * 手机验证码注册
     */
    Boolean smsRegister(SmsRegisterDTO smsLoginDTO);

}
