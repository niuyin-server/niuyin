package com.niuyin.service.behave.controller;


import com.niuyin.model.social.UserFavorite;
import com.niuyin.service.behave.service.IUserFavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (UserFavorite)表控制层
 *
 * @author lzq
 * @since 2023-11-13 16:37:53
 */
@RestController
@RequestMapping("/userFavorite")
public class UserFavoriteController {
    
    @Resource
    private IUserFavoriteService userFavoriteService;


}

