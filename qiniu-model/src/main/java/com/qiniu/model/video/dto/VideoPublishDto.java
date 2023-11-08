package com.qiniu.model.video.dto;

import com.qiniu.model.video.domain.Video;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/29 14:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoPublishDto extends Video {

    //分类名称
    private Long categoryId;
}
