package com.niuyin.model.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * LoginUserDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Data
public class SmsLoginDTO {

    @NotBlank
    private String telephone;

    @NotBlank
    private String smsCode;
}
