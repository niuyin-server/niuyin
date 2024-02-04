package com.niuyin.service.behave.controller.app;

import com.niuyin.common.domain.vo.PageDataInfo;
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
@RequestMapping("/api/v1/app/favorite")
public class AppVideoUserFavoritesController {

    @Resource
    private IVideoUserFavoritesService videoUserFavoritesService;

    /**
     * 分页我的收藏
     */
    @PostMapping("/myPage")
    public PageDataInfo myFavoritePageForApp(@RequestBody VideoPageDto pageDto) {
        return videoUserFavoritesService.queryUserFavoriteVideoPageForApp(pageDto);
    }

}

