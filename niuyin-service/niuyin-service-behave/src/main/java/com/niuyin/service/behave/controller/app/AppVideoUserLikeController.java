package com.niuyin.service.behave.controller.app;

import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.service.behave.service.IVideoUserLikeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 点赞表(VideoUserLike)表控制层
 *
 * @author lzq
 * @since 2023-10-30 14:32:56
 */
@RestController
@RequestMapping("/api/v1/app/like")
public class AppVideoUserLikeController {

    @Resource
    private IVideoUserLikeService videoUserLikeService;

    /**
     * 用户点赞
     */
    @GetMapping("/{videoId}")
    public R<Boolean> userLikeAction(@PathVariable("videoId") String videoId) {
        return R.ok(videoUserLikeService.videoActionLike(videoId));
    }

    /**
     * 用户取消点赞
     */
    @GetMapping("/unlike/{videoId}")
    public R<Boolean> userUnlikeAction(@PathVariable("videoId") String videoId) {
        return R.ok(videoUserLikeService.videoActionUnlike(videoId));
    }

    /**
     * 我的点赞分页查询
     */
    @PostMapping("/myLikePage")
    public PageDataInfo myLikePageForApp(@RequestBody VideoPageDto pageDto) {
        return videoUserLikeService.queryMyLikeVideoPageForApp(pageDto);
    }

    /**
     * 分页用户点赞列表
     */
    @PostMapping("/userPage")
    public PageDataInfo personLikePage(@RequestBody VideoPageDto pageDto) {
        return videoUserLikeService.queryPersonLikePage(pageDto);
    }

}

