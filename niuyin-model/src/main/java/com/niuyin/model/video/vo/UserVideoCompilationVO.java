package com.niuyin.model.video.vo;

import com.niuyin.model.video.domain.UserVideoCompilation;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * UserVideoCompilationVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/27
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserVideoCompilationVO extends UserVideoCompilation {
    // 播放量
    private Long viewCount;
    // 获赞量
    private Long likeCount;
    // 被收藏数
    private Long favoriteCount;
    // 视频数
    private Long videoCount;
}
