package com.niuyin.service.behave.controller.app;


import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.model.behave.domain.UserFavorite;
import com.niuyin.service.behave.service.IUserFavoriteService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (UserFavorite)表控制层
 *
 * @author lzq
 * @since 2023-11-13 16:37:53
 */
@RestController
@RequestMapping("/api/v1/app/userFavorite")
public class AppUserFavoriteController {

    @Resource
    private IUserFavoriteService userFavoriteService;

    /**
     * 我的收藏夹集合
     */
    @GetMapping("/list/{videoId}")
    public R<?> userFavoriteList(@PathVariable("videoId")String videoId) {
        return R.ok(userFavoriteService.userFavoritesFolderList(videoId));
    }

    /**
     * 新建收藏夹
     */
    @PostMapping()
    public R<?> newFavorite(@RequestBody UserFavorite userFavorite) {
        userFavorite.setUserId(UserContext.getUserId());
        return R.ok(userFavoriteService.saveFavorite(userFavorite));
    }

}

