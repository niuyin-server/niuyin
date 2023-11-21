package com.niuyin.model.video.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 视频定位表(VideoPosition)实体类
 *
 * @author roydon
 * @since 2023-11-21 15:44:14
 */
@Data
@TableName("video_position")
public class VideoPosition implements Serializable {
    private static final long serialVersionUID = 649827621807953953L;
    /**
     * 位置id
     */
    @TableId(value = "position_id", type = IdType.AUTO)
    private Long positionId;
    /**
     * 视频id
     */
    private String videoId;
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 纬度
     */
    private Double latitude;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 城市code
     */
    private String cityCode;
    /**
     * 区
     */
    private String district;
    /**
     * 邮编
     */
    private String adcode;
    /**
     * 地址
     */
    private String address;
    /**
     * 状态标志（0：启用、1：禁用）
     */
    private String status;


}

