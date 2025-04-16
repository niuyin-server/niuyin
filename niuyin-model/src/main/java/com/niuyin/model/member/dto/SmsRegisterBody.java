package com.niuyin.model.member.dto;

import lombok.Data;

/**
 * RegisterBody
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Data
public class SmsRegisterBody{

    /**
     * 用户名
     */
    private String telephone;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户密码
     */
    private String confirmPassword;
}
