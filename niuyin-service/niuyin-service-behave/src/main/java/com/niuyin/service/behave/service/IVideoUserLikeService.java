package com.niuyin.service.behave.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.behave.domain.VideoUserLike;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.dto.VideoPageDto;

import java.util.List;

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

    /**
     * 查询用户的点赞列表
     *
     * @param pageDto
     * @return
     */
    List<Video> queryPersonLikePage(VideoPageDto pageDto);
}
