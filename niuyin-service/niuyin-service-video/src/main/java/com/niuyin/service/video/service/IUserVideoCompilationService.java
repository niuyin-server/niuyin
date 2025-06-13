package com.niuyin.service.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.model.video.domain.UserVideoCompilation;
import com.niuyin.model.video.dto.CompilationVideoPageDTO;
import com.niuyin.model.video.dto.UpdateUserVideoCompilationDTO;
import com.niuyin.model.video.dto.UserVideoCompilationPageDTO;
import com.niuyin.model.video.vo.UserVideoCompilationInfoVO;

/**
 * 用户视频合集表(UserVideoCompilation)表服务接口
 *
 * @author roydon
 * @since 2023-11-27 18:08:39
 */
public interface IUserVideoCompilationService extends IService<UserVideoCompilation> {

    /**
     * 分页查询我的合集
     *
     * @param pageDTO
     * @return
     */
    PageData videoCompilationMyPage(UserVideoCompilationPageDTO pageDTO);

    /**
     * 分页查询用户合集
     *
     * @param pageDTO
     * @return
     */
    PageData videoCompilationUserPage(UserVideoCompilationPageDTO pageDTO);

    /**
     * 合集播放量
     */
    Long compilationViewCount(Long compilationId);

    /**
     * 获赞量
     */
    Long compilationLikeCount(Long compilationId);

    /**
     * 被收藏数
     */
    Long compilationFavoriteCount(Long compilationId);

    /**
     * 视频数
     */
    Long compilationVideoCount(Long compilationId);

    /**
     * 更新视频合集
     */
    Boolean updateVideoCompilationInfo(UpdateUserVideoCompilationDTO updateUserVideoCompilationDTO);

    /**
     * 合集视频分页
     */
    PageData compilationVideoPage(CompilationVideoPageDTO pageDTO);

    /**
     * 根据视频id获取合集信息
     *
     * @param videoId
     * @return
     */
    UserVideoCompilationInfoVO getCompilationInfoVOByVideoId(String videoId);
}
