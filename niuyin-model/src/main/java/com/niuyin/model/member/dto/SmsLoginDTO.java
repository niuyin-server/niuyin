package com.niuyin.model.member.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

/**
 * LoginUserDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Data
@ApiModel("手机短信登陆体")
public class SmsLoginDTO {

    @ApiModelProperty("手机号码")
    @NotBlank
    private String telephone;

    @ApiModelProperty("登录验证码")
    @NotBlank
    private String smsCode;
}
