package com.niuyin.service.behave.controller.v1;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.model.behave.domain.UserFavoriteVideo;
import com.niuyin.model.behave.dto.UserFavoriteVideoDTO;
import com.niuyin.service.behave.service.IUserFavoriteVideoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (UserFavoriteVideo)表控制层
 *
 * @author lzq
 * @since 2023-11-17 10:16:03
 */
@RestController
@RequestMapping("/api/v1/userFavoriteVideo")
public class UserFavoriteVideoController {

    @Resource
    private IUserFavoriteVideoService userFavoriteVideoService;

    /**
     * 收藏视频
     */
    @PostMapping()
    public R<Boolean> favoriteVideoToCollection(@Validated @RequestBody UserFavoriteVideoDTO userFavoriteVideoDTO) {
        return R.ok(userFavoriteVideoService.videoFavorites(userFavoriteVideoDTO));
    }

    /**
     * 根据视频id查询被哪些收藏夹收藏
     */
    @GetMapping("/{videoId}")
    public R<Long[]> getVideoUserCollection(@PathVariable("videoId") String videoId) {
        LambdaQueryWrapper<UserFavoriteVideo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFavoriteVideo::getUserId, UserContext.getUserId());
        queryWrapper.eq(UserFavoriteVideo::getVideoId, videoId);
        Long[] array = userFavoriteVideoService.list(queryWrapper).stream().map(UserFavoriteVideo::getFavoriteId).toArray(Long[]::new);
        return R.ok(array);
    }

}

