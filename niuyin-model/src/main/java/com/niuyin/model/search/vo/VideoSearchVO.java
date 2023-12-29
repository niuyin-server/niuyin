package com.niuyin.model.search.vo;

import lombok.Data;

import java.util.Date;

/**
 * VideoSearchVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 * 视频搜索返回体
 **/
@Data
public class VideoSearchVO {

    // 视频id
    private String videoId;
    // 标题
    private String videoTitle;
    // 文章发布时间
    private Date publishTime;
    // 视频封面
    private String coverImage;
    // 视频地址
    private String videoUrl;
    private String publishType;
    // 用户id
    private Long userId;
    // 用户昵称
    private String userNickName;
    // 用户头像
    private String userAvatar;

}
