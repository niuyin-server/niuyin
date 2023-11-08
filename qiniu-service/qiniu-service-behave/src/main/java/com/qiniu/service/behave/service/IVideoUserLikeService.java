package com.qiniu.service.behave.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.video.domain.VideoUserLike;
import com.qiniu.model.video.dto.VideoPageDto;

/**
 * 点赞表(VideoUserLike)表服务接口
 *
 * @author lzq
 * @since 2023-10-30 14:33:00
 */
public interface IVideoUserLikeService extends IService<VideoUserLike> {

    boolean videoLike(String videoId);

    /**
     * 分页查询我的视频
     *
     * @param pageDto
     * @return
     */
    IPage<VideoUserLike> queryMyLikeVideoPage(VideoPageDto pageDto);
}
