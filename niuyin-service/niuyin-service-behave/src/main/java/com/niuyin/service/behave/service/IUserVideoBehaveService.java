package com.niuyin.service.behave.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.behave.domain.UserVideoBehave;
import com.niuyin.model.behave.enums.UserVideoBehaveEnum;

/**
 * 用户视频行为表(UserVideoBehave)表服务接口
 *
 * @author roydon
 * @since 2024-04-19 14:21:13
 */
public interface IUserVideoBehaveService extends IService<UserVideoBehave> {

    /**
     * 同步用户行为到表 UserVideoBehave
     *
     * @param userId
     * @param videoId
     * @param behave
     * @return
     */
    Boolean syncUserVideoBehave(Long userId, String videoId, UserVideoBehaveEnum behave);

}
