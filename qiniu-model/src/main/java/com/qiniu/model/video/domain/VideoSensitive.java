package com.qiniu.model.video.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 敏感词信息表(VideoSensitive)实体类
 *
 * @author lzq
 * @since 2023-10-30 11:17:39
 */
@Data
@TableName("video_sensitive")
@ApiModel("视频敏感名")
public class VideoSensitive implements Serializable {

    private static final long serialVersionUID = 887830254999098288L;
    /**
     * 主键
     */
    @ApiModelProperty("id")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    /**
     * 敏感词
     */
    @ApiModelProperty("敏感词")
    private String sensitives;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;


}

