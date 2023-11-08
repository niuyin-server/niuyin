package com.qiniu.model.video.dto;

import com.qiniu.model.video.domain.Video;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/29 19:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoPageDto extends Video {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

