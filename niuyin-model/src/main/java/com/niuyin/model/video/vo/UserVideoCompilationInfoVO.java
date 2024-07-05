package com.niuyin.model.video.vo;

import com.niuyin.model.video.domain.UserVideoCompilation;
import lombok.Data;

/**
 * web页面
 * 大屏播放视频合集tab
 *
 * @AUTHOR: roydon
 * @DATE: 2024/5/15
 **/
@Data
public class UserVideoCompilationInfoVO extends UserVideoCompilation {
    private Long playCount;
    private Long favoriteCount;
    private Long videoCount;
    private Boolean weatherFollow;
}
