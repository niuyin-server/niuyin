package com.niuyin.model.social;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * (UserFavorite)实体类
 *
 * @author lzq
 * @since 2023-11-13 14:56:12
 */
@Data
@TableName("")
public class UserFavorite implements Serializable {
    private static final long serialVersionUID = 449453316357090990L;
/**
     *  收藏夹id
     */
    private Long favoriteId;
/**
     * 用户id
     */
    private Long userId;
/**
     * 收藏夹名称
     */
    private String title;
/**
     * 收藏夹描述
     */
    private String description;
/**
     * 收藏夹封面
     */
    private String coverImage;
/**
     * 创建时间
     */
    private LocalDateTime createTime;
/**
     * 0:别人可见，1:陌生人不可见
     */
    private String showStatus;
/**
     * 0存在，1删除
     */
    private String delFlag;


}

