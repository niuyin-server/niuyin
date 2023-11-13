package com.niuyin.service.behave.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.social.UserFavorite;
import com.niuyin.service.behave.mapper.UserFavoriteMapper;
import com.niuyin.service.behave.service.IUserFavoriteService;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;

/**
 * (UserFavorite)表服务实现类
 *
 * @author lzq
 * @since 2023-11-13 16:37:53
 */
@Service("userFavoriteService")
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements IUserFavoriteService {
    @Resource
    private UserFavoriteMapper userFavoriteMapper;

}
