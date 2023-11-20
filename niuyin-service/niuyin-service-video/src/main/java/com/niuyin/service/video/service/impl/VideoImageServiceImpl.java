package com.niuyin.service.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.video.domain.VideoImage;
import com.niuyin.service.video.mapper.VideoImageMapper;
import com.niuyin.service.video.service.IVideoImageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 视频图片关联表(VideoImage)表服务实现类
 *
 * @author roydon
 * @since 2023-11-20 21:19:00
 */
@Service("videoImageService")
public class VideoImageServiceImpl extends ServiceImpl<VideoImageMapper, VideoImage> implements IVideoImageService {
    @Resource
    private VideoImageMapper videoImageMapper;

}
