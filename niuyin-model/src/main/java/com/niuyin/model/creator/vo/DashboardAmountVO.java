package com.niuyin.model.creator.vo;

import lombok.Data;

/**
 * DashboardAmountVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/11
 **/
@Data
public class DashboardAmountVO {
    // 播放量
    private DashboardAmountItem playAmount;
    // 主页访问量
    private DashboardAmountItem indexViewAmount;
    // 净增粉丝量
    private DashboardAmountItem fansAmount;
    // 作品点赞量
    private DashboardAmountItem likeAmount;
    // 评论量
    private DashboardAmountItem commentAmount;
    // 分享量
    private DashboardAmountItem shareAmount;
}
