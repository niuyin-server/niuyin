package com.qiniu.model.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 用户敏感词信息表(UserSensitive)实体类
 *
 * @author roydon
 * @since 2023-10-29 20:36:06
 */
@Data
@TableName("user_sensitive")
public class UserSensitive implements Serializable {
    private static final long serialVersionUID = 565052820117877580L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 敏感词
     */
    private String sensitives;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;


}

