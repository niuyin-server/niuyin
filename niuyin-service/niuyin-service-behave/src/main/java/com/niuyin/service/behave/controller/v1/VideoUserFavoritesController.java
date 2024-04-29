package com.niuyin.service.behave.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.behave.domain.VideoUserFavorites;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.service.behave.service.IVideoUserFavoritesService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 视频收藏表(VideoUserFavorites)表控制层
 *
 * @author lzq
 * @since 2023-10-31 15:57:37
 */
@RestController
@RequestMapping("/api/v1/favorite")
public class VideoUserFavoritesController {

    @Resource
    private IVideoUserFavoritesService videoUserFavoritesService;

    /**
     * 用户仅收藏视频
     */
    @GetMapping("/{videoId}")
    public R<Boolean> userFavoriteOnlyVideo(@PathVariable("videoId") String videoId) {
        return R.ok(videoUserFavoritesService.userOnlyFavoriteVideo(videoId));
    }

    /**
     * 取消收藏视频
     */
    @PutMapping("/unFavorite/{videoId}")
    public R<Boolean> userUnFavoriteVideo(@PathVariable("videoId") String videoId) {
        return R.ok(videoUserFavoritesService.userUnFavoriteVideo(videoId));
    }

    /**
     * 分页我的收藏
     *
     * @param pageDto
     * @return
     */
    @PostMapping("/mypage")
    public PageDataInfo myFavoritePage(@RequestBody VideoPageDto pageDto) {
        return videoUserFavoritesService.queryUserFavoriteVideoPage(pageDto);
    }

    @DeleteMapping("/{videoId}")
    public R<?> deleteVideoFavoriteRecordByVideoId(@PathVariable String videoId) {
        LambdaQueryWrapper<VideoUserFavorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserFavorites::getVideoId, videoId);
        return R.ok(videoUserFavoritesService.removeById(videoId));
    }

    /**
     * 用户是否收藏某视频
     *
     * @param videoId
     * @return
     */
    @GetMapping("/weather/{videoId}")
    public R<Boolean> weatherFavorite(@PathVariable("videoId") String videoId) {
        LambdaQueryWrapper<VideoUserFavorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserFavorites::getVideoId, videoId);
        queryWrapper.eq(VideoUserFavorites::getUserId, UserContext.getUserId());
        return R.ok(videoUserFavoritesService.count(queryWrapper) > 0);
    }

    /**
     * 我的作品收藏数
     */
    @GetMapping("/favoriteCount")
    public R<Long> countFavorite() {
        return R.ok(videoUserFavoritesService.count(new LambdaQueryWrapper<VideoUserFavorites>().eq(VideoUserFavorites::getUserId, UserContext.getUserId())));
    }
}

