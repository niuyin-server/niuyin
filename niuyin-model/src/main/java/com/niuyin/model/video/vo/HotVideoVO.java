package com.niuyin.model.video.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * HotVideoVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/7
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class HotVideoVO extends VideoVO{
    /**
     * 视频分值
     */
    private Double score;
}
