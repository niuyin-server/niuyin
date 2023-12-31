package com.niuyin.service.behave.controller.v1;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niuyin.common.domain.R;
import com.niuyin.model.social.UserFavorite;
import com.niuyin.service.behave.service.IUserFavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/list/{userId}")
    public R<?> getUserFavoriteList(@PathVariable("userId") Long userId){
        LambdaQueryWrapper<UserFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFavorite::getUserId, userId);
        return R.ok(userFavoriteService.list(queryWrapper));
    }

}

