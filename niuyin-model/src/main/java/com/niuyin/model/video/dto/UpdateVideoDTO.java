package com.niuyin.model.video.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * UpdateVideoDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/27
 **/
@Data
public class UpdateVideoDTO {
    /**
     * 视频ID
     */
    private String videoId;

    @Size(min = 1, max = 100, message = "标题需在100字符以内")
    private String videoTitle;

    @Size(min = 1, max = 200, message = "描述需在200字符以内")
    private String videoDesc;
    /**
     * 视频封面地址
     */
    private String coverImage;
    /**
     * 展示类型（0全部可见1好友可见2自己可见）
     */
    private String showType;
    /**
     * 定位功能0关闭1开启
     */
    private String positionFlag;
}
