package com.qiniu.model.video.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 视频分类关联表(VideoCategoryRelation)实体类
 *
 * @author lzq
 * @since 2023-10-31 14:44:34
 */
@Data
@TableName("video_category_relation")
@ApiModel("视频分类关联表")
public class VideoCategoryRelation implements Serializable {
    private static final long serialVersionUID = -53464330298328844L;

    /**
     * 视频id
     */
    @ApiModelProperty("视频id")
    private String videoId;
    /**
     * 分类id
     */
    @ApiModelProperty("分类id")
    private Long categoryId;


}

