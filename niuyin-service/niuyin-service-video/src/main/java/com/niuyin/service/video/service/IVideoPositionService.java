package com.niuyin.service.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.video.domain.VideoPosition;

/**
 * 视频定位表(VideoPosition)表服务接口
 *
 * @author roydon
 * @since 2023-11-21 15:44:15
 */
public interface IVideoPositionService extends IService<VideoPosition> {

    /**
     * 通过视频id获取定位信息
     *
     * @param videoId
     * @return
     */
    VideoPosition queryPositionByVideoId(String videoId);

}
