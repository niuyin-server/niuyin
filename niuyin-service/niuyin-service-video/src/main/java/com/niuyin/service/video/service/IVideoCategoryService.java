package com.niuyin.service.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.video.domain.VideoCategory;
import com.niuyin.model.video.dto.CategoryVideoPageDTO;
import com.niuyin.model.video.dto.VideoCategoryPageDTO;
import com.niuyin.model.video.vo.VideoCategoryTree;
import com.niuyin.model.video.vo.VideoCategoryVo;
import com.niuyin.model.video.vo.VideoPushVO;
import com.niuyin.model.video.vo.app.AppVideoCategoryVo;

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

    /**
     * 获取视频分类树
     */
    List<VideoCategoryTree> getCategoryTree();

    /**
     * 获取所有可用视频父分类
     */
    List<AppVideoCategoryVo> getNormalParentCategory();

    /**
     * 获取一级子分类
     *
     * @param id
     * @return
     */
    List<AppVideoCategoryVo> getNormalChildrenCategory(Long id);

    /**
     * 根据分类id分页获取视频
     *
     * @param pageDTO
     * @return
     */
    PageDataInfo getVideoPageByCategoryId(CategoryVideoPageDTO pageDTO);

    /**
     * 获取视频父分类集合
     *
     * @return
     */
    List<VideoCategory> getVideoParentCategoryList();

}
