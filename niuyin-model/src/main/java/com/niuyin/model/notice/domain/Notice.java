package com.niuyin.model.notice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 通知表(Notice)实体类
 *
 * @author roydon
 * @since 2023-11-08 16:21:44
 */
@Data
@TableName("notice")
public class Notice implements Serializable {
    private static final long serialVersionUID = -60869533333103048L;
    /**
     * 通知id
     */
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Long noticeId;
    /**
     * 用户id
     */
    private Long operateUserId;
    // 被通知的用户
    private Long noticeUserId;
    /**
     * 内容
     */
    private String content;
    /**
     * 通知类型(0：点赞，1：关注，2：收藏、3：回复评论、4：赞了评论)
     */
    private String noticeType;
    /**
     * 接收标志(0：未读 1：已读)
     */
    private String receiveFlag;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}

