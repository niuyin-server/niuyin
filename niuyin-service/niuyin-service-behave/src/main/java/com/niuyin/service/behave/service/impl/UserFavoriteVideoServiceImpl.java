package com.niuyin.service.behave.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.behave.domain.UserFavoriteVideo;
import com.niuyin.service.behave.mapper.UserFavoriteVideoMapper;
import com.niuyin.service.behave.service.IUserFavoriteVideoService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;

/**
 * (UserFavoriteVideo)表服务实现类
 *
 * @author lzq
 * @since 2023-11-17 10:16:09
 */
@Service("userFavoriteVideoService")
public class UserFavoriteVideoServiceImpl extends ServiceImpl<UserFavoriteVideoMapper, UserFavoriteVideo> implements IUserFavoriteVideoService {
    @Resource
    private UserFavoriteVideoMapper userFavoriteVideoMapper;


}
