package com.qiniu.model.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RegisterBody
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterBody extends LoginUserDTO{

    /**
     * 用户密码
     */
    private String confirmPassword;
}
