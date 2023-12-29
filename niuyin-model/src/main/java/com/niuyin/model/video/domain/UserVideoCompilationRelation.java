package com.niuyin.model.video.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户视频合集与视频关联表(UserVideoCompilationRelation)实体类
 *
 * @author roydon
 * @since 2023-12-08 20:21:12
 */
@Data
@TableName("user_video_compilation_relation")
public class UserVideoCompilationRelation implements Serializable {
    private static final long serialVersionUID = 396530646359931561L;
    /**
     * 合集id
     */
    private Long compilationId;
    /**
     * 视频id
     */
    private String videoId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}

