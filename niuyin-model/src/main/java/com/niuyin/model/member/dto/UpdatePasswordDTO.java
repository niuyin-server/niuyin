package com.niuyin.model.member.dto;

import lombok.Data;

/**
 * 修改密码请求体
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/29
 **/
@Data
public class UpdatePasswordDTO {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
