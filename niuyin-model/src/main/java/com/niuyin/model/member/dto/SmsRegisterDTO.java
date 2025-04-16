package com.niuyin.model.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 手机短信注册体
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Data
public class SmsRegisterDTO {

    @NotBlank
    private String telephone;

    @NotBlank
    private String smsCode;

    @NotBlank
    @Size(min = 6, max = 18, message = "密码长度须在6~18字符")
    private String password;

    @NotBlank
    @Size(min = 6, max = 18, message = "密码长度须在6~18字符")
    private String confirmPassword;
}
