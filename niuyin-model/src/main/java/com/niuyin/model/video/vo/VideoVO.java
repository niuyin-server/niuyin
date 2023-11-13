package com.niuyin.model.video.vo;

import com.niuyin.model.video.domain.Video;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * VideoVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoVO extends Video {
    private Long commentNum;
    private String userNickName;
    private String userAvatar;
    // 是否点赞
    private boolean weatherLike;
    // 是否收藏
    private boolean weatherFavorite;
    // 是否关注
    private boolean weatherFollow;
    // 标签数组
    private String[] tags;

    // 热力值
    private Double hotScore;
}
