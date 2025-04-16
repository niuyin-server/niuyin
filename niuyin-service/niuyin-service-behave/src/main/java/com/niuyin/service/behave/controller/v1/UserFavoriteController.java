package com.niuyin.service.behave.controller.v1;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.behave.domain.UserFavorite;
import com.niuyin.model.behave.vo.UserFavoriteInfoVO;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.service.behave.mapper.UserFavoriteMapper;
import com.niuyin.service.behave.service.IUserFavoriteService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
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

    @Resource
    private UserFavoriteMapper userFavoriteMapper;

    /**
     * 我的收藏夹集合
     */
    @GetMapping("/list")
    public R<?> userFavoriteList() {
        LambdaQueryWrapper<UserFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFavorite::getUserId, UserContext.getUserId());
        return R.ok(userFavoriteService.list(queryWrapper));
    }

    /**
     * 我的收藏夹集合分页查询
     */
    @PostMapping("/page")
    public PageDataInfo userFavoritePage(@RequestBody PageDTO pageDTO) {
        IPage<UserFavorite> userFavoriteIPage = userFavoriteService.queryCollectionPage(pageDTO);
        return PageDataInfo.genPageData(userFavoriteIPage.getRecords(), userFavoriteIPage.getTotal());
    }

    /**
     * 我的收藏夹详情集合
     */
    @GetMapping("/infoList")
    public R<List<UserFavoriteInfoVO>> userCollectionInfoList() {
        return R.ok(userFavoriteService.queryCollectionInfoList());
    }

    /**
     * 我的收藏夹集合详情分页查询
     */
    @PostMapping("/infoPage")
    public PageDataInfo userCollectionInfoPage(@RequestBody PageDTO pageDTO) {
        return userFavoriteService.queryMyCollectionInfoPage(pageDTO);
    }

    /**
     * 新建收藏夹
     */
    @PostMapping()
    public R<?> newFavorite(@RequestBody UserFavorite userFavorite) {
        userFavorite.setUserId(UserContext.getUserId());
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
        userFavorite.setUserId(UserContext.getUserId());
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

