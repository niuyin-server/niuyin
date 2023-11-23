package com.niuyin.service.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.video.domain.VideoImage;

import java.util.List;

/**
 * 视频图片关联表(VideoImage)表服务接口
 *
 * @author roydon
 * @since 2023-11-20 21:19:00
 */
public interface IVideoImageService extends IService<VideoImage> {

    /**
     * 通过视频id查询视频图片
     *
     * @param videoId
     * @return
     */
    List<VideoImage> queryImagesByVideoId(String videoId);

}
