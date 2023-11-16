package com.niuyin.service.behave.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.model.behave.domain.UserFavorite;
import com.niuyin.service.behave.enums.UserFavoriteStatus;
import com.niuyin.service.behave.mapper.UserFavoriteMapper;
import com.niuyin.service.behave.service.IUserFavoriteService;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.time.LocalDateTime;

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



    /**
     * @param userFavorite
     * @return
     */
    @Override
    public boolean saveFavorite(UserFavorite userFavorite) {

        UserFavorite userFavoriteDb = BeanCopyUtils.copyBean(userFavorite, UserFavorite.class);
        userFavoriteDb.setDelFlag(UserFavoriteStatus.NORMAL.getCode());
        userFavoriteDb.setCreateTime(LocalDateTime.now());
        boolean save = this.save(userFavoriteDb);
//        if (save){
//
//        }
        return save;
    }
}
