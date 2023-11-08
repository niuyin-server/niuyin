package com.qiniu.model.social;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

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

}

