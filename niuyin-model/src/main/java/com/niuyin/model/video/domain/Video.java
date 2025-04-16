package com.niuyin.model.video.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 视频表(Video)实体类
 *
 * @author roydon
 * @since 2023-10-25 20:33:10
 */
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

    @Size(min = 1, max = 100, message = "标题需在100字符以内")
    private String videoTitle;

    @Size(min = 1, max = 200, message = "描述需在200字符以内")
    private String videoDesc;
    /**
     * 视频封面地址
     */
    private String coverImage;
    /**
     * 视频地址
     */
    private String videoUrl;
    private Long viewNum;
    private Long likeNum;
    private Long favoritesNum;
    /**
     * 发布类型（0视频，1图文）
     */
    private String publishType;
    /**
     * 展示类型（0全部可见1好友可见2自己可见）
     */
    private String showType;
    /**
     * 定位功能0关闭1开启
     */
    private String positionFlag;
    /**
     * 审核状态(0:待审核1:审核成功2:审核失败)
     */
    private String auditsStatus;
    /**
     * 视频详情
     */
    private String videoInfo;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    /**
     * 更新者
     */
    private String updateBy;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}

