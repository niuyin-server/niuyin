package com.niuyin.service.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.video.domain.VideoCategoryRelation;
import com.niuyin.service.video.mapper.VideoCategoryRelationMapper;
import com.niuyin.service.video.service.IVideoCategoryRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 视频分类关联表(VideoCategoryRelation)表服务实现类
 *
 * @author lzq
 * @since 2023-10-31 14:44:35
 */
@Service("videoCategoryRelationService")
public class VideoCategoryRelationServiceImpl extends ServiceImpl<VideoCategoryRelationMapper, VideoCategoryRelation> implements IVideoCategoryRelationService {
    @Resource
    private VideoCategoryRelationMapper videoCategoryRelationMapper;

    @Override
    public boolean saveVideoCategoryRelation(VideoCategoryRelation videoCategoryRelation) {
        return this.save(videoCategoryRelation);
    }
}
