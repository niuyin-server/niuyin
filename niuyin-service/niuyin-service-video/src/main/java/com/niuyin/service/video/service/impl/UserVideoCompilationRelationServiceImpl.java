package com.niuyin.service.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.video.domain.UserVideoCompilationRelation;
import com.niuyin.service.video.mapper.UserVideoCompilationRelationMapper;
import com.niuyin.service.video.service.IUserVideoCompilationRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户视频合集与视频关联表(UserVideoCompilationRelation)表服务实现类
 *
 * @author roydon
 * @since 2023-12-08 20:21:13
 */
@Service("userVideoCompilationRelationService")
public class UserVideoCompilationRelationServiceImpl extends ServiceImpl<UserVideoCompilationRelationMapper, UserVideoCompilationRelation> implements IUserVideoCompilationRelationService {
    @Resource
    private UserVideoCompilationRelationMapper userVideoCompilationRelationMapper;

    /**
     * 将视频添加到合集
     *
     * @param videoId
     * @param compilationId
     * @return
     */
    @Override
    public Boolean videoRelateCompilation(String videoId, Long compilationId) {
        UserVideoCompilationRelation userVideoCompilationRelation = new UserVideoCompilationRelation();
        userVideoCompilationRelation.setCompilationId(compilationId);
        userVideoCompilationRelation.setVideoId(videoId);
        return this.save(userVideoCompilationRelation);
    }
}
