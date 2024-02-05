package com.niuyin.model.behave.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 点赞表(VideoUserLike)实体类
 *
 * @author lzq
 * @since 2023-10-30 14:32:59
 */
@Data
@ApiModel("视频点赞表")
@TableName("video_user_like")
public class VideoUserLike implements Serializable {
    private static final long serialVersionUID = 366516787359335038L;
    /**
     * 点赞表id，记录总数即为点赞总数
     */
    @ApiModelProperty("点赞表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 视频id
     */
    @ApiModelProperty("视频id")
    private String videoId;
    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long userId;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;


}

