package com.niuyin.service.behave.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.feign.video.RemoteVideoService;
import com.niuyin.model.behave.domain.VideoUserFavorites;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.behave.domain.VideoUserLike;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.service.behave.service.IVideoUserLikeService;
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
     * todo 使用sql 用户点赞分页查询
     */
    @PostMapping("/mylikepage")
    public PageDataInfo myLikePage(@RequestBody VideoPageDto pageDto) {
        IPage<VideoUserLike> likeIPage = videoUserLikeService.queryMyLikeVideoPage(pageDto);
        List<String> videoIds = likeIPage.getRecords().stream().map(VideoUserLike::getVideoId).collect(Collectors.toList());
        if (videoIds.isEmpty()) {
            return PageDataInfo.emptyPage();
        }
        List<Video> videos = remoteVideoService.queryVideoByVideoIds(videoIds).getData();
        return PageDataInfo.genPageData(videos, likeIPage.getTotal());
    }

    /**
     * 取消点赞
     *
     * @param videoId
     * @return
     */
    @DeleteMapping("/{videoId}")
    public R<?> deleteVideoLikeRecord(@PathVariable String videoId) {
        LambdaQueryWrapper<VideoUserLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserLike::getVideoId, videoId);
        return R.ok(videoUserLikeService.remove(queryWrapper));
    }

    /**
     * 用户是否点赞某视频
     *
     * @param videoId
     * @return
     */
    @GetMapping("/weather/{videoId}")
    public R<Boolean> weatherLike(@PathVariable("videoId") String videoId) {
        LambdaQueryWrapper<VideoUserLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserLike::getVideoId, videoId);
        queryWrapper.eq(VideoUserLike::getUserId, UserContext.getUserId());
        return R.ok(videoUserLikeService.count(queryWrapper) > 0);
    }

    /**
     * 我的喜欢数
     */
    @GetMapping("/likeCount")
    public R<Long> countFavorite() {
        return R.ok(videoUserLikeService.count(new LambdaQueryWrapper<VideoUserLike>().eq(VideoUserLike::getUserId, UserContext.getUserId())));
    }

    /**
     * 查询用户的点赞列表
     *
     * @param pageDto
     * @return
     */
    @PostMapping("/personLikePage")
    public PageDataInfo personLikePage(@RequestBody VideoPageDto pageDto) {
        List<Video> likeIPage = videoUserLikeService.queryPersonLikePage(pageDto);
        return PageDataInfo.genPageData(likeIPage, likeIPage.size());
    }

}

