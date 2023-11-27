package com.niuyin.model.video.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 用户视频合集表(UserVideoCompilation)实体类
 *
 * @author roydon
 * @since 2023-11-27 18:08:39
 */
@Data
@TableName("user_video_compilation")
public class UserVideoCompilation implements Serializable {
    private static final long serialVersionUID = -19314287338282438L;
    /**
     * compilation_id
     */
    @TableId(value = "compilation_id", type = IdType.AUTO)
    private Long compilationId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 合集标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 合集封面(5M)
     */
    private String coverImage;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;


}

