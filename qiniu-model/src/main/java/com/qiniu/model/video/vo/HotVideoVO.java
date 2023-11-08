package com.qiniu.model.video.vo;

import lombok.Data;

/**
 * HotVideoVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/7
 **/
@Data
public class HotVideoVO extends VideoVO{
    /**
     * 视频分值
     */
    private Long score;
}
