package com.niuyin.model.social.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户关注表(UserFollow)实体类
 *
 * @author roydon
 * @since 2023-10-30 15:54:20
 */
@Data
@AllArgsConstructor
@TableName("user_follow")
public class UserFollow implements Serializable {
    private static final long serialVersionUID = -88140804672872294L;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 被关注用户ID
     */
    private Long userFollowId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}

