package com.qiniu.model.video.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * (VideoCategory)实体类
 *
 * @author lzq
 * @since 2023-10-30 19:41:13
 */
@Data
@TableName("video_category")
@ApiModel("视频分类表")
public class VideoCategory implements Serializable {
    private static final long serialVersionUID = 449072917820489412L;
    /**
     * 视频分类id
     */
    @ApiModelProperty("视频分类id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 视频分类名称
     */
    @ApiModelProperty("视频分类名称")
    private String name;
    /**
     * 视频分类描述
     */
    @ApiModelProperty("视频分类描述")
    private String description;
    private String status; //0可用，1禁用

}

