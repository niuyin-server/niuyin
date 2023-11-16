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
     * 用户新家收藏夹
     *
     * @param userFavorite
     * @return
     */
    @Override
    public boolean saveFavorite(UserFavorite userFavorite) {
        //将传过来的参数copy到要在数据库存储的对象中
        UserFavorite userFavoriteDb = BeanCopyUtils.copyBean(userFavorite, UserFavorite.class);
        //从枚举类中获取默认的删除标志参数
        userFavoriteDb.setDelFlag(UserFavoriteStatus.NORMAL.getCode());
        //设置创建时间
        userFavoriteDb.setCreateTime(LocalDateTime.now());
        boolean save = this.save(userFavoriteDb);
        //todo  新建收藏夹保存成功之后，将消息发送到mq
        return save;
    }
}
