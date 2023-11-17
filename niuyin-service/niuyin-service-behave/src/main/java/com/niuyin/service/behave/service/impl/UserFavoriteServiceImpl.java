package com.niuyin.service.behave.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.model.behave.domain.UserFavorite;
import com.niuyin.model.common.enums.DelFlagEnum;
import com.niuyin.service.behave.mapper.UserFavoriteMapper;
import com.niuyin.service.behave.service.IUserFavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * (UserFavorite)表服务实现类
 *
 * @author lzq
 * @since 2023-11-13 16:37:53
 */
@Slf4j
@Service("userFavoriteService")
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements IUserFavoriteService {
    @Resource
    private UserFavoriteMapper userFavoriteMapper;

    /**
     * 用户新建收藏夹
     *
     * @param userFavorite
     * @return
     */
    @Override
    public boolean saveFavorite(UserFavorite userFavorite) {
        //从token中获取userId
        userFavorite.setUserId(UserContext.getUserId());
        //从枚举类中获取默认的删除标志参数
        userFavorite.setDelFlag(DelFlagEnum.EXIST.getCode());
        userFavorite.setCreateTime(LocalDateTime.now());
        return this.save(userFavorite);
    }
}
