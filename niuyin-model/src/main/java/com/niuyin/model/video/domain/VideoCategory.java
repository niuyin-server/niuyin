package com.niuyin.model.video.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    private String categoryImage;  //分类图片，推荐64*64的icon图标
    /**
     * 视频分类描述
     */
    @ApiModelProperty("视频分类描述")
    private String description;
    private String visible;
    /**
     * 分类状态：(0:可用,1:禁用)
     */
    private String status;

    private Long parentId;

    private Integer orderNum;

    private String createBy;
    private String updateBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    private String remark;

}

