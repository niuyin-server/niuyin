package com.qiniu.model.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * LoginUserDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Data
@ApiModel("账号密码登陆体")
public class LoginUserDTO {
    /**
     * 用户名
     */
    @ApiModelProperty("账号")
    private String username;

    /**
     * 用户密码
     */
    @ApiModelProperty("密码")
    private String password;
}
