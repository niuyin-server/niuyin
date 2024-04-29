package com.niuyin.service.recommend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.behave.domain.UserVideoBehave;
import com.niuyin.service.recommend.mapper.UserVideoBehaveMapper;
import com.niuyin.service.recommend.service.IUserVideoBehaveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户视频行为表(UserVideoBehave)表服务实现类
 *
 * @author roydon
 * @since 2024-04-27 18:56:49
 */
@Service("userVideoBehaveService")
public class UserVideoBehaveServiceImpl extends ServiceImpl<UserVideoBehaveMapper, UserVideoBehave> implements IUserVideoBehaveService {

    @Resource
    private UserVideoBehaveMapper userVideoBehaveMapper;

}
