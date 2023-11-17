package com.niuyin.service.behave.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.feign.video.RemoteVideoService;
import com.niuyin.model.behave.domain.UserFavoriteVideo;
import com.niuyin.model.behave.domain.VideoUserFavorites;
import com.niuyin.model.behave.dto.UserFavoriteVideoDTO;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.service.behave.service.IVideoUserFavoritesService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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

    @Resource
    private RemoteVideoService remoteVideoService;


    /**
     * 用户收藏
     *
     * @param videoId
     * @return
     */
    @GetMapping("/{videoId}")
    public R<Boolean> getDetails(@PathVariable String videoId) {
        return R.ok(videoUserFavoritesService.videoFavorites(videoId));
    }

    /**
     * 分页我的收藏
     *
     * @param pageDto
     * @return
     */
    @PostMapping("/myfavoritepage")
    public PageDataInfo myFavoritePage(@RequestBody VideoPageDto pageDto) {
        IPage<VideoUserFavorites> favoritesPage = videoUserFavoritesService.queryFavoritePage(pageDto);
        List<String> videoIds = favoritesPage.getRecords().stream().map(VideoUserFavorites::getVideoId).collect(Collectors.toList());
        if (videoIds.isEmpty()) {
            return PageDataInfo.emptyPage();
        }
        return PageDataInfo.genPageData(remoteVideoService.queryVideoByVideoIds(videoIds).getData(), favoritesPage.getTotal());
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

