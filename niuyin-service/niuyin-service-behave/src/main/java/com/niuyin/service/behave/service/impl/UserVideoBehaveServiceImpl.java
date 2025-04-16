package com.niuyin.service.behave.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.behave.domain.UserVideoBehave;
import com.niuyin.model.behave.enums.UserVideoBehaveEnum;
import com.niuyin.service.behave.mapper.UserVideoBehaveMapper;
import com.niuyin.service.behave.service.IUserVideoBehaveService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 用户视频行为表(UserVideoBehave)表服务实现类
 *
 * @author roydon
 * @since 2024-04-19 14:21:15
 */
@Service("userVideoBehaveService")
public class UserVideoBehaveServiceImpl extends ServiceImpl<UserVideoBehaveMapper, UserVideoBehave> implements IUserVideoBehaveService {
    @Resource
    private UserVideoBehaveMapper userVideoBehaveMapper;

    /**
     * 同步用户行为到表 UserVideoBehave
     *
     * @param userId
     * @param videoId
     * @param behave
     * @return
     */
    @Async
    @Override
    public Boolean syncUserVideoBehave(Long userId, String videoId, UserVideoBehaveEnum behave) {
        UserVideoBehave userVideoBehave = new UserVideoBehave();
        userVideoBehave.setUserId(userId);
        userVideoBehave.setUserBehave(behave.getCode());
        userVideoBehave.setVideoId(videoId);
        return userVideoBehaveMapper.syncUserVideoBehave(userVideoBehave);
    }
}
