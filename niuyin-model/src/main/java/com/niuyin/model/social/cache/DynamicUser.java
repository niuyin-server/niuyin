package com.niuyin.model.social.cache;

import lombok.Data;

/**
 * DynamicUser
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/7
 **/
@Data
public class DynamicUser {
    private Long userId;
    private String nickname;
    private String avatar;
    // 是否查看过动态
    private Boolean hasRead;
    // todo 最后一次拉取动态的时间
//    private Long lastPullTime;
    // todo 拉取的动态数量
//    private Long pullVideoNCount;
}
