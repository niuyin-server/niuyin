package com.niuyin.model.member.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 会员详情表(MemberInfo)实体类
 *
 * @author roydon
 * @since 2023-11-12 22:26:25
 */
@Data
@TableName("member_info")
public class MemberInfo implements Serializable {
    private static final long serialVersionUID = -18427092522208701L;
    /**
     * id
     */
    @TableId(value = "info_id", type = IdType.AUTO)
    private Long infoId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 个人页面背景图片
     */
    private String backImage;
    /**
     * 个人描述
     */
    private String description;
    /**
     * 生日
     */
    private LocalDateTime birthday;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 邮编
     */
    private String adcode;
    /**
     * 学校
     */
    private String campus;

}

