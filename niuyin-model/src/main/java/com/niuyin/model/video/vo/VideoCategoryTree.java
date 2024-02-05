package com.niuyin.model.video.vo;

import com.niuyin.model.video.domain.VideoCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 视频分类树
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/5
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoCategoryTree extends VideoCategory {

    private List<VideoCategory> children;

}
