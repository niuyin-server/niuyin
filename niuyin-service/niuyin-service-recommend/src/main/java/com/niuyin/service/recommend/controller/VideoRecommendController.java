package com.niuyin.service.recommend.controller;

import com.niuyin.common.core.domain.R;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.recommend.service.VideoRecommendService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 视频推荐控制器
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/27
 **/
@RestController
@RequestMapping("/api/v1/video")
public class VideoRecommendController {

    @Resource
    private VideoRecommendService videoRecommendService;

    /**
     * 初始化推荐列表
     */
    @GetMapping("/init")
    public R<?> init() {
        return R.ok();
    }

    /**
     * 获取推荐视频流
     */
    @GetMapping("/feed")
    public R<List<VideoVO>> videoFeed() {
        return R.ok(videoRecommendService.pullVideoFeed());
    }

}
