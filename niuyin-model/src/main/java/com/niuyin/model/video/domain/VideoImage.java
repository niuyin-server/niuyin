package com.niuyin.model.video.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 视频图片关联表(VideoImage)实体类
 *
 * @author roydon
 * @since 2023-11-20 21:18:59
 */
@Data
@TableName("video_image")
public class VideoImage implements Serializable {
    private static final long serialVersionUID = -25511217734667800L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 视频ID
     */
    private String videoId;
    /**
     * 图片地址
     */
    private String imageUrl;


}

