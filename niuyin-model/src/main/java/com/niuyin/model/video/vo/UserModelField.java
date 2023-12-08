package com.niuyin.model.video.vo;

import lombok.Data;

/**
 * 用户模型field
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/6
 **/
@Data
public class UserModelField {
    private String tag;
    private Long videoId;
    private Double score;
}
