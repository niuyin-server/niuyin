package com.niuyin.service.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.video.domain.VideoImage;
import com.niuyin.service.video.mapper.VideoImageMapper;
import com.niuyin.service.video.service.IVideoImageService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 视频图片关联表(VideoImage)表服务实现类
 *
 * @author roydon
 * @since 2023-11-20 21:19:00
 */
@Service
public class VideoImageServiceImpl extends ServiceImpl<VideoImageMapper, VideoImage> implements IVideoImageService {
    @Resource
    private VideoImageMapper videoImageMapper;

    /**
     * 通过视频id查询视频图片
     *
     * @param videoId
     * @return
     */
    @Override
    public List<VideoImage> queryImagesByVideoId(String videoId) {
        LambdaQueryWrapper<VideoImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoImage::getVideoId, videoId);
        return this.list(queryWrapper);
    }

    /**
     * 删除视频
     *
     * @param videoId
     * @return
     */
    @Override
    public boolean deleteVideoImagesByVideoId(String videoId) {
        LambdaQueryWrapper<VideoImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoImage::getVideoId, videoId);
        return this.remove(queryWrapper);
    }
}
