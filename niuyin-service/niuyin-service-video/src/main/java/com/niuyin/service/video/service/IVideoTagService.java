package com.niuyin.service.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.video.domain.VideoTag;

/**
 * 视频标签表(VideoTag)表服务接口
 *
 * @author roydon
 * @since 2023-11-11 16:05:09
 */
public interface IVideoTagService extends IService<VideoTag> {

    /**
     * 保存标签
     *
     * @param videoTag
     * @return
     */
    VideoTag saveTag(VideoTag videoTag);

    /**
     * 根据tag返回标签
     *
     * @param tag
     * @return
     */
    VideoTag queryByTag(String tag);

}
