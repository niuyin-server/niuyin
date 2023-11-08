package com.qiniu.model.video.vo;

import com.qiniu.model.video.domain.Video;
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
    // 是否关注
    private boolean weatherFollow;

    // 热力值
    private Double hotScore;
}
