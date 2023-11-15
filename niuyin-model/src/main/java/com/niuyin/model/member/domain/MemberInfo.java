package com.niuyin.model.member.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Size;
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
    @Size(max = 255, message = "背景图地址过长")
    private String backImage;
    /**
     * 个人描述
     */
    @Size(max = 300, message = "简介不可超过300字符")
    private String description;
    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime birthday;
    /**
     * 省
     */
    @Size(max = 20, message = "省份名称过长")
    private String province;
    /**
     * 市
     */
    @Size(max = 30, message = "城市名过长")
    private String city;
    /**
     * 区
     */
    @Size(max = 30, message = "区名过长")
    private String region;
    /**
     * 邮编
     */
    @Size(max = 6, message = "邮编支持6位字符")
    private String adcode;
    /**
     * 学校
     */
    @Size(max = 64, message = "学校名称过长")
    private String campus;
    /**
     * 喜欢视频向外展示状态：0展示1隐藏
     */
    private String likeShowStatus;

    /**
     * 收藏视频向外展示状态：0展示1隐藏
     */
    private String favoriteShowStatus;

    /**
     * 喜欢视频向外展示状态：0展示1隐藏
     */
    private String likeShowStatus;

    /**
     * 收藏视频向外展示状态：0展示1隐藏
     */
    private String favoriteShowStatus;

}

