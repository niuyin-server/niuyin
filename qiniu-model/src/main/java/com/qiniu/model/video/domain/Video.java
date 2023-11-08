package com.qiniu.model.video.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 视频表(Video)实体类
 *
 * @author roydon
 * @since 2023-10-25 20:33:10
 */
@ApiModel("视频表-video")
@Data
@TableName("video")
public class Video implements Serializable {
    private static final long serialVersionUID = 665174769504081543L;
    /**
     * 视频ID
     */
    @TableId("video_id")
    private String videoId;
    /**
     * 用户id
     */
    private Long userId;
    private String videoTitle;
    private String videoDesc;
    /**
     * 视频地址
     */
    private String videoUrl;
    private Long viewNum;
    private Long likeNum;
    private Long favoritesNum;
    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private String delFlag;
    /**
     * 创建者
     */
    private String createBy;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新者
     */
    private String updateBy;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 视频封面地址
     */
    private String coverImage;

}

