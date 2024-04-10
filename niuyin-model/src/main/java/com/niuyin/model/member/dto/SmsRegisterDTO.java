package com.niuyin.model.member.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 手机短信注册体
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Data
@ApiModel("手机短信注册体")
public class SmsRegisterDTO {

    @ApiModelProperty("手机号码")
    @NotBlank
    private String telephone;

    @ApiModelProperty("注册验证码")
    @NotBlank
    private String smsCode;

    @ApiModelProperty("密码")
    @NotBlank
    @Size(min = 6, max = 18, message = "密码长度须在6~18字符")
    private String password;

    @ApiModelProperty("确认密码")
    @NotBlank
    @Size(min = 6, max = 18, message = "密码长度须在6~18字符")
    private String confirmPassword;
}
