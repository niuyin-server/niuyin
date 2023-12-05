package com.niuyin.model.creator.dto;

import lombok.Data;

/**
 * VideoPageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/5
 **/
@Data
public class VideoPageDTO {
    /**
     * 视频标题
     */
    private String videoTitle;
    /**
     * 发布类型（0视频，1图文）
     */
    private String publishType;
    /**
     * 展示类型（0全部可见1好友可见2自己可见）
     */
    private String showType;
    /**
     * 定位功能0关闭1开启
     */
    private String positionFlag;
    /**
     * 审核状态(0:待审核；1:审核成功；2:审核失败)
     */
    private String auditsStatus;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
