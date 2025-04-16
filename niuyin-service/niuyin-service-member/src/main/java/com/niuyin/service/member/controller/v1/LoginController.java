package com.niuyin.service.member.controller.v1;

import com.niuyin.common.core.constant.Constants;
import com.niuyin.common.core.domain.R;
import com.niuyin.model.member.dto.SmsLoginDTO;
import com.niuyin.model.member.enums.LoginTypeEnum;
import com.niuyin.service.member.service.LoginService;
import com.niuyin.service.member.strategy.context.LoginStrategyContext;
import com.niuyin.starter.sms.domain.model.SmsCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录控制器
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/28
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class LoginController {

    @Resource
    private LoginService loginService;

    @Resource
    LoginStrategyContext loginStrategyContext;

    /**
     * 发送登录验证码
     *
     * @param telephone 电话
     * @return code
     */
    @GetMapping("/loginAuthCode/{telephone}")
    public R<String> sendCode(@PathVariable("telephone") String telephone) {
        SmsCode smsCode = loginService.sendLoginAuthCode(telephone);
        return R.ok(smsCode.getCode());
    }

    /**
     * 手机短信登录
     */
    @PostMapping("/sms-login")
    public R<Map<String, String>> smsLogin(@Validated @RequestBody SmsLoginDTO smsLoginDTO) {
//        String token = loginService.smsLogin(smsLoginDTO);
        String token = loginStrategyContext.executeLoginStrategy(smsLoginDTO, LoginTypeEnum.SMS);
        Map<String, String> map = new HashMap<>();
        map.put(Constants.TOKEN, token);
        return R.ok(map);
    }

}
