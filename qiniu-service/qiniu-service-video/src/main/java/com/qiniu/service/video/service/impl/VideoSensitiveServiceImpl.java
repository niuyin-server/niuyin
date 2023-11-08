package com.qiniu.service.video.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.model.video.domain.VideoSensitive;
import com.qiniu.service.video.mapper.VideoSensitiveMapper;
import com.qiniu.service.video.service.IVideoSensitiveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 敏感词信息表(VideoSensitive)表服务实现类
 *
 * @author lzq
 * @since 2023-10-30 11:34:03
 */
@Service("videoSensitiveService")
public class VideoSensitiveServiceImpl extends ServiceImpl<VideoSensitiveMapper, VideoSensitive> implements IVideoSensitiveService {
    @Resource
    private VideoSensitiveMapper videoSensitiveMapper;

}
