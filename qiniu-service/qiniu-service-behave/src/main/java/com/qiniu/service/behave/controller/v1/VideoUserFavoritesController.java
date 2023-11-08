package com.qiniu.service.behave.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qiniu.common.domain.R;
import com.qiniu.common.domain.vo.PageDataInfo;
import com.qiniu.feign.video.RemoteVideoService;
import com.qiniu.model.video.domain.VideoUserFavorites;
import com.qiniu.model.video.domain.VideoUserLike;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.service.behave.service.IVideoUserFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @Resource
//    private IVideoService videoService;

    @Resource
    private RemoteVideoService remoteVideoService;


    /**
     * 用户收藏
     *
     * @param videoId
     * @return
     */
    @GetMapping("/{videoId}")
    public R<Boolean> getDetails(@PathVariable("videoId") String videoId) {

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
        if (videoIds.isEmpty()){
            return PageDataInfo.genPageData(null,0);
        }
        return PageDataInfo.genPageData(remoteVideoService.queryVideoByVideoIds(videoIds).getData(), favoritesPage.getTotal());
    }

    @DeleteMapping("/{videoId}")
    public R<?> deleteVideoFavoriteRecordByVideoId(@PathVariable String videoId){
        LambdaQueryWrapper<VideoUserFavorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserFavorites::getVideoId,videoId);
        return R.ok(videoUserFavoritesService.removeById(videoId));
    }
}

