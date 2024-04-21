package com.niuyin.model.social.vo;

import lombok.Data;

/**
 * FollowUser
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/21
 * 关注用户分页vo
 **/
@Data
public class FollowUser {
    private Long userId;
    private String avatar;
    private String nickName;
    private String sex;
}
