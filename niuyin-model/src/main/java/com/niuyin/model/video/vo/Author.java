package com.niuyin.model.video.vo;

import lombok.Data;

/**
 * 视频作者
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/20
 **/
@Data
public class Author {
    private Long userId;
    private String userName;
    private String nickName;
    private String avatar;
}
