package com.niuyin.service.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.video.domain.VideoPosition;
import com.niuyin.service.video.mapper.VideoPositionMapper;
import com.niuyin.service.video.service.IVideoPositionService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 视频定位表(VideoPosition)表服务实现类
 *
 * @author roydon
 * @since 2023-11-21 15:44:15
 */
@Service("videoPositionService")
public class VideoPositionServiceImpl extends ServiceImpl<VideoPositionMapper, VideoPosition> implements IVideoPositionService {
    @Resource
    private VideoPositionMapper videoPositionMapper;

    /**
     * 通过视频id获取定位信息
     *
     * @param videoId
     * @return
     */
    @Override
    public VideoPosition queryPositionByVideoId(String videoId) {
        LambdaQueryWrapper<VideoPosition> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoPosition::getVideoId, videoId);
        return this.getOne(queryWrapper);
    }

    /**
     * 删除视频定位
     *
     * @param videoId
     * @return
     */
    @Override
    public boolean deleteRecordByVideoId(String videoId) {
        LambdaQueryWrapper<VideoPosition> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoPosition::getVideoId, videoId);
        return this.remove(queryWrapper);
    }
}
