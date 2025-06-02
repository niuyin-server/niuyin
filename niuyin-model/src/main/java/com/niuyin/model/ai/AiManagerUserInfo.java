package com.niuyin.model.ai;

import lombok.Data;

/**
 * AiManagerUserInfo
 *
 * @AUTHOR: roydon
 * @DATE: 2025/5/31
 **/
@Data
public class AiManagerUserInfo extends AiManagerDO {
    /**
     * 用户账号
     */
    private String userName;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 头像地址
     */
    private String avatar;
}
