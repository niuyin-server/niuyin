package com.niuyin.service.video.controller.app;

import com.niuyin.common.domain.R;
import com.niuyin.model.video.vo.app.VideoRecommendVO;
import com.niuyin.service.video.service.IVideoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 视频表(Video)表控制层
 *
 * @author roydon
 * @since 2023-10-25 20:33:08
 */
@RestController
@RequestMapping("/api/v1/app")
public class AppVideoController {

    @Resource
    private IVideoService videoService;

    /**
     * 首页推送视频
     *
     * @return
     */
    @GetMapping("/recommend")
    public R<List<VideoRecommendVO>> appPushVideo() {
        return R.ok(videoService.pushAppVideoList());
    }

}
