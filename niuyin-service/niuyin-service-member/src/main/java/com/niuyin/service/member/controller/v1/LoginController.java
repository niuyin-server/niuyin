package com.niuyin.service.member.controller.v1;

import cn.hutool.core.util.PhoneUtil;
import com.niuyin.common.constant.Constants;
import com.niuyin.common.domain.R;
import com.niuyin.common.utils.PhoneUtils;
import com.niuyin.model.member.dto.LoginUserDTO;
import com.niuyin.model.member.dto.SmsLoginDTO;
import com.niuyin.service.member.service.LoginService;
import com.niuyin.starter.sms.domain.model.SmsCode;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    /**
     * 发送登录验证码
     *
     * @param telephone 电话
     * @return code
     */
    @ApiOperation("登录验证码")
    @GetMapping("/loginAuthCode/{telephone}")
    public R<String> sendCode(@PathVariable("telephone") String telephone) {
        SmsCode smsCode = loginService.sendLoginAuthCode(telephone);
        return R.ok(smsCode.getCode());
    }

    /**
     * 手机短信登录
     */
    @ApiOperation("登录")
    @PostMapping("/sms-login")
    public R<Map<String, String>> smsLogin(@Validated @RequestBody SmsLoginDTO smsLoginDTO) {
        String token = loginService.smsLogin(smsLoginDTO);
        Map<String, String> map = new HashMap<>();
        map.put(Constants.TOKEN, token);
        return R.ok(map);
    }

}
