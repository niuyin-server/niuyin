package com.niuyin.service.behave.controller.v1;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.R;
import com.niuyin.model.behave.domain.UserFavorite;
import com.niuyin.model.behave.vo.UserFavoriteInfoVO;
import com.niuyin.service.behave.service.IUserFavoriteService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (UserFavorite)表控制层
 *
 * @author lzq
 * @since 2023-11-13 16:37:53
 */
@RestController
@RequestMapping("/api/v1/userFavorite")
public class UserFavoriteController {

    @Resource
    private IUserFavoriteService userFavoriteService;

    /**
     * 我的收藏夹集合
     */
    @GetMapping("/list")
    public R<?> getUserFavoriteList() {
        LambdaQueryWrapper<UserFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFavorite::getUserId, UserContext.getUserId());
        return R.ok(userFavoriteService.list(queryWrapper));
    }

    /**
     * 我的收藏夹详情集合
     */
    @GetMapping("/infoList")
    public R<List<UserFavoriteInfoVO>> userCollectionInfoList() {
        return R.ok(userFavoriteService.queryCollectionInfoList());
    }

    /**
     * 新建收藏夹
     */
    @PostMapping()
    public R<?> newFavorite(@RequestBody UserFavorite userFavorite) {
        return R.ok(userFavoriteService.saveFavorite(userFavorite));
    }

    /**
     * 根据id获取
     *
     * @param favoriteId
     * @return
     */
    @GetMapping("/{favoriteId}")
    public R<UserFavorite> getInfoById(@PathVariable("favoriteId") Long favoriteId) {
        LambdaQueryWrapper<UserFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFavorite::getUserId, UserContext.getUserId());
        queryWrapper.eq(UserFavorite::getFavoriteId, favoriteId);
        return R.ok(userFavoriteService.getOne(queryWrapper));
    }

    /**
     * 更新收藏夹
     */
    @PutMapping()
    public R<Boolean> updateFavorite(@RequestBody UserFavorite userFavorite) {
        return R.ok(userFavoriteService.updateById(userFavorite));
    }

    /**
     * 删除收藏夹
     *
     * @param favoriteId
     * @return
     */
    @DeleteMapping("/{favoriteId}")
    public R<Boolean> deleteFavorite(@PathVariable("favoriteId") Long favoriteId) {
        return R.ok(userFavoriteService.removeById(favoriteId));
    }

}

