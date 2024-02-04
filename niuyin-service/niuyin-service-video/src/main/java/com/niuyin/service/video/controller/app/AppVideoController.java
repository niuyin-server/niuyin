package com.niuyin.service.video.controller.app;

import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.model.video.vo.app.VideoInfoVO;
import com.niuyin.model.video.vo.app.VideoRecommendVO;
import com.niuyin.service.video.service.IVideoService;
import org.springframework.cache.annotation.Cacheable;
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
     */
    @GetMapping("/recommend")
    public R<List<VideoRecommendVO>> appPushVideo() {
        return R.ok(videoService.pushAppVideoList());
    }

    /**
     * 视频详情
     */
    @GetMapping("/info/{videoId}")
    public R<VideoInfoVO> appVideoInfo(@PathVariable("videoId") String videoId) {
        return R.ok(videoService.getVideoInfoForApp(videoId));
    }

    /**
     * 热门视频分页
     */
    @PostMapping("/hotVideo")
    @Cacheable(value = "hotVideos", key = "'hotVideos'+#pageDTO.pageNum + '_' + #pageDTO.pageSize")
    public PageDataInfo hotVideosForApp(@RequestBody PageDTO pageDTO) {
        return videoService.getHotVideos(pageDTO);
    }

    /**
     * 分页查询我的视频
     */
    @PostMapping("/myPage")
    public PageDataInfo myPageForApp(@RequestBody VideoPageDto pageDto) {
        return videoService.queryMyVideoPageForApp(pageDto);
    }


}
