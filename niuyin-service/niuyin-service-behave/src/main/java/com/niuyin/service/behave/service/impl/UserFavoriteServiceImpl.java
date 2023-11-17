package com.niuyin.service.behave.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.model.behave.domain.UserFavorite;
import com.niuyin.service.behave.enums.UserFavoriteStatus;
import com.niuyin.service.behave.mapper.UserFavoriteMapper;
import com.niuyin.service.behave.service.IUserFavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.niuyin.model.common.enums.HttpCodeEnum.HAS_ERROR;
import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_DIRECT_EXCHANGE;
import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_CREATE_ROUTING_KEY;

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

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 用户新建收藏夹
     *
     * @param userFavorite
     * @return
     */
    @Override
    public boolean saveFavorite(UserFavorite userFavorite) {
        //将传过来的参数copy到要在数据库存储的对象中
        UserFavorite userFavoriteDb = BeanCopyUtils.copyBean(userFavorite, UserFavorite.class);
        //从token中获取userId
        userFavoriteDb.setUserId(UserContext.getUserId());
        //从枚举类中获取默认的删除标志参数
        userFavoriteDb.setDelFlag(UserFavoriteStatus.NORMAL.getCode());
        userFavoriteDb.setCreateTime(LocalDateTime.now());
        return this.save(userFavoriteDb);
    }
}
