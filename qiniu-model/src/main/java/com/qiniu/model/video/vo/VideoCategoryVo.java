package com.qiniu.model.video.vo;

import com.qiniu.model.video.domain.VideoCategory;
import lombok.Data;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/11/1 11:56
 */
@Data
public class VideoCategoryVo {
    /**
     * 分类id
     */
    private Long id;
    /**
     * 分类name
     */
    private String name;
}
