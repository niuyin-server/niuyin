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
 * 视频收藏表(VideoUserFavorites)实体类
 * 此表表示用户仅仅收藏视频，与收藏夹无关
 *
 * @author lzq
 * @since 2023-10-31 15:57:38
 */
@Data
@TableName("video_user_favorites")
@ApiModel("用户视频收藏表")
public class VideoUserFavorites implements Serializable {
    private static final long serialVersionUID = -50448230889868246L;
    /**
     * 收藏表id，总数即为s
     */
    @ApiModelProperty("收藏表id")
    @TableId(value = "id",type = IdType.AUTO)
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

