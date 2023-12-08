package com.niuyin.service.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.video.domain.UserVideoCompilationRelation;

/**
 * 用户视频合集与视频关联表(UserVideoCompilationRelation)表服务接口
 *
 * @author roydon
 * @since 2023-12-08 20:21:13
 */
public interface IUserVideoCompilationRelationService extends IService<UserVideoCompilationRelation> {

    /**
     * 将视频添加到合集
     *
     * @param videoId
     * @param compilationId
     * @return
     */
    Boolean videoRelateCompilation(String videoId, Long compilationId);

}
