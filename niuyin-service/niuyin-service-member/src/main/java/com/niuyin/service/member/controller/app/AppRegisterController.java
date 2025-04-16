package com.niuyin.service.member.controller.app;

import com.niuyin.common.core.domain.R;
import com.niuyin.model.member.dto.SmsRegisterDTO;
import com.niuyin.service.member.service.RegisterService;
import com.niuyin.starter.sms.domain.model.SmsCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * AppLoginController
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/10
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1/app")
public class AppRegisterController {

    @Resource
    private RegisterService registerService;

    /**
     * 发送注册验证码
     */
    @GetMapping("/registerAuthCode/{telephone}")
    public R<String> sendCode(@PathVariable("telephone") String telephone) {
        SmsCode smsCode = registerService.sendRegisterAuthCode(telephone);
        return R.ok(smsCode.getCode());
    }

    /**
     * 手机短信注册
     */
    @PostMapping("/sms-register")
    public R<Boolean> smsLogin(@Validated @RequestBody SmsRegisterDTO smsRegisterDTO) {
        return R.ok(registerService.smsRegister(smsRegisterDTO));
    }

}
