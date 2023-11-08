package com.qiniu.model.video.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
     * 新闻id
     */
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
    private String content;
    /**
     * 状态：0默认1禁止
     */
    private String status;

    private LocalDateTime createTime;

}

