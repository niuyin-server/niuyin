package com.niuyin.service.video.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoCategory;
import com.niuyin.model.video.dto.VideoCategoryPageDTO;
import com.niuyin.model.video.vo.VideoCategoryVo;
import com.niuyin.model.video.vo.VideoPushVO;
import com.niuyin.model.video.vo.VideoVO;

import java.util.List;
import java.util.Set;

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
     *
     * @param pageDTO
     * @return
     */
    PageDataInfo selectVideoByCategory(VideoCategoryPageDTO pageDTO);

    /**
     * 根据分类推送视频
     *
     * @param categoryId
     * @return
     */
    List<VideoPushVO> pushVideoByCategory(Long categoryId);
}
