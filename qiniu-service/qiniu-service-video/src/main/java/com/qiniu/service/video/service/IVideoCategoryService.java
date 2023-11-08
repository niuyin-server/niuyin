package com.qiniu.service.video.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.domain.VideoCategory;
import com.qiniu.model.video.dto.VideoCategoryPageDTO;
import com.qiniu.model.video.vo.VideoCategoryVo;

import java.util.List;

/**
 * (VideoCategory)表服务接口
 *
 * @author lzq
 * @since 2023-10-30 19:41:14
 */
public interface IVideoCategoryService extends IService<VideoCategory> {

    List<VideoCategory> saveVideoCategoriesToRedis();

    List<VideoCategoryVo> selectAllCategory();

    /**
     * 分页根据分类获取视频
     * @param pageDTO
     * @return
     */
    IPage<Video> selectVideoByCategory(VideoCategoryPageDTO pageDTO);
}
