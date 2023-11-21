package com.niuyin.service.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.video.domain.VideoPosition;
import com.niuyin.service.video.mapper.VideoPositionMapper;
import com.niuyin.service.video.service.IVideoPositionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
}
