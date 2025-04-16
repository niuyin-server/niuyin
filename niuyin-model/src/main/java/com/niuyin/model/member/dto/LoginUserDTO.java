package com.niuyin.model.member.dto;

import lombok.Data;

/**
 * LoginUserDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Data
public class LoginUserDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;
}
