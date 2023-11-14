package com.niuyin.model.video.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 视频标签关联表(VideoTagRelation)实体类
 *
 * @author roydon
 * @since 2023-11-11 17:19:09
 */
@Data
@TableName("video_tag_relation")
public class VideoTagRelation implements Serializable {
    private static final long serialVersionUID = -77325744334344037L;
    /**
     * 视频id
     */
    private String videoId;
    /**
     * 标签id
     */
    private Long tagId;

}

