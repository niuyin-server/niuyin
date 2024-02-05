package com.niuyin.service.member.service;

import com.niuyin.model.member.dto.SmsLoginDTO;
import com.niuyin.starter.sms.domain.model.SmsCode;

/**
 * LoginService
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/28
 **/
public interface LoginService {

    /**
     * 发送登录验证码
     * @param telephone
     * @return
     */
    SmsCode sendLoginAuthCode(String telephone);

    /**
     * 手机验证码登录
     */
    String smsLogin(SmsLoginDTO smsLoginDTO);

}
