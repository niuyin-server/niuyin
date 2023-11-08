package com.qiniu.service.behave.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qiniu.common.domain.R;
import com.qiniu.common.domain.vo.PageDataInfo;
import com.qiniu.feign.video.RemoteVideoService;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.domain.VideoUserLike;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.service.behave.service.IVideoUserLikeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 点赞表(VideoUserLike)表控制层
 *
 * @author lzq
 * @since 2023-10-30 14:32:56
 */
@RestController
@RequestMapping("/api/v1/like")
public class VideoUserLikeController {

    @Resource
    private IVideoUserLikeService videoUserLikeService;

    @Resource
    private RemoteVideoService remoteVideoService;

    /**
     * 用户点赞
     *
     * @param videoId
     * @return
     */
    @GetMapping("/{videoId}")
    public R<Boolean> getDetails(@PathVariable("videoId") String videoId) {
        return R.ok(videoUserLikeService.videoLike(videoId));
    }

    /**
     * 用户点赞分页查询
     */
    @PostMapping("/mylikepage")
    public PageDataInfo myLikePage(@RequestBody VideoPageDto pageDto) {
        IPage<VideoUserLike> likeIPage = videoUserLikeService.queryMyLikeVideoPage(pageDto);
        List<String> videoIds = likeIPage.getRecords().stream().map(VideoUserLike::getVideoId).collect(Collectors.toList());
        if (videoIds.isEmpty()) {
            return PageDataInfo.genPageData(null, 0);
        }
//        List<Video> videos = videoService.queryVideoByVideoIds(videoIds );
        List<Video> videos = remoteVideoService.queryVideoByVideoIds(videoIds).getData();
        return PageDataInfo.genPageData(videos, likeIPage.getTotal());
    }

    @DeleteMapping("/{videoId}")
    public R<?> deleteVideoLikeRecord(@PathVariable String videoId){
        LambdaQueryWrapper<VideoUserLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserLike::getVideoId,videoId);
        return R.ok(videoUserLikeService.remove(queryWrapper));
    }

}

