package com.niuyin.model.behave.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * (VideoUserComment)实体类
 *
 * @author roydon
 * @since 2023-10-30 16:52:51
 */
@Data
@TableName("video_user_comment")
public class VideoUserComment implements Serializable {
    private static final long serialVersionUID = -19005250815450923L;
    /**
     * 评论id
     */
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;
    /**
     * 视频id
     */
    @NotNull
    private String videoId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 父id
     */
    private Long parentId;
    /**
     * 评论的根id
     */
    private Long originId;
    /**
     * 评论内容
     */
    @NotBlank
    @Size(max = 300, message = "评论内容不能超过300字符")
    private String content;
    /**
     * 状态：0默认1禁止
     */
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}

