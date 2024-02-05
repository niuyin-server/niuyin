package com.niuyin.model.video.vo;

import lombok.Data;

/**
 * 相关视频推荐返回体
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/14
 **/
@Data
public class RelateVideoVO {

    private String videoId;
    private String videoTitle;
    /**
     * 视频封面地址
     */
    private String coverImage;
    /**
     * 用户id
     */
    private Long userId;
    private String userNickName;

    private Long likeNum;
}
