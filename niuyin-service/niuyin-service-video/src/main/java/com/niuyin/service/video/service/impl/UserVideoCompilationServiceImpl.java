package com.niuyin.service.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.video.domain.UserVideoCompilation;
import com.niuyin.model.video.dto.CompilationVideoPageDTO;
import com.niuyin.model.video.dto.UpdateUserVideoCompilationDTO;
import com.niuyin.model.video.dto.UserVideoCompilationPageDTO;
import com.niuyin.model.video.vo.CompilationVideoVO;
import com.niuyin.model.video.vo.UserVideoCompilationInfoVO;
import com.niuyin.model.video.vo.UserVideoCompilationVO;
import com.niuyin.service.video.mapper.UserVideoCompilationMapper;
import com.niuyin.service.video.service.IUserVideoCompilationRelationService;
import com.niuyin.service.video.service.IUserVideoCompilationService;
import com.niuyin.service.video.service.IVideoService;
import com.niuyin.service.video.util.PackageUserVideoCompilationVOProcessor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视频合集表(UserVideoCompilation)表服务实现类
 *
 * @author roydon
 * @since 2023-11-27 18:08:39
 */
@Service("userVideoCompilationService")
public class UserVideoCompilationServiceImpl extends ServiceImpl<UserVideoCompilationMapper, UserVideoCompilation> implements IUserVideoCompilationService {
    @Resource
    private UserVideoCompilationMapper userVideoCompilationMapper;

    @Resource
    @Lazy
    private PackageUserVideoCompilationVOProcessor packageUserVideoCompilationVOProcessor;

    @Resource
    private IUserVideoCompilationRelationService userVideoCompilationRelationService;

    @Resource
    private IVideoService videoService;

    /**
     * 分页查询我的合集
     *
     * @param pageDTO
     * @return
     */
    @Override
    public PageData videoCompilationMyPage(UserVideoCompilationPageDTO pageDTO) {
        LambdaQueryWrapper<UserVideoCompilation> uvcQW = new LambdaQueryWrapper<>();
        uvcQW.eq(UserVideoCompilation::getUserId, UserContext.getUserId());
        uvcQW.like(StringUtils.isNotEmpty(pageDTO.getTitle()), UserVideoCompilation::getTitle, pageDTO.getTitle());
        uvcQW.orderByDesc(UserVideoCompilation::getCreateTime);
        IPage<UserVideoCompilation> userVideoCompilationIPage = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), uvcQW);
        List<UserVideoCompilation> records = userVideoCompilationIPage.getRecords();
        if (records.isEmpty()) {
            return PageData.emptyPage();
        }
        List<UserVideoCompilationVO> userVideoCompilationVOList = BeanCopyUtils.copyBeanList(records, UserVideoCompilationVO.class);
        // 封装VO
        packageUserVideoCompilationVOProcessor.processUserVideoCompilationVOList(userVideoCompilationVOList);
        return PageData.genPageData(userVideoCompilationVOList, userVideoCompilationIPage.getTotal());
    }

    /**
     * 分页查询用户合集
     *
     * @param pageDTO
     * @return
     */
    @Override
    public PageData videoCompilationUserPage(UserVideoCompilationPageDTO pageDTO) {
        Long userId = pageDTO.getUserId();
        if (StringUtils.isNull(userId)) {
            return PageData.emptyPage();
        }
        LambdaQueryWrapper<UserVideoCompilation> uvcQW = new LambdaQueryWrapper<>();
        uvcQW.eq(UserVideoCompilation::getUserId, userId);
        uvcQW.like(StringUtils.isNotEmpty(pageDTO.getTitle()), UserVideoCompilation::getTitle, pageDTO.getTitle());
        uvcQW.orderByDesc(UserVideoCompilation::getCreateTime);
        IPage<UserVideoCompilation> userVideoCompilationIPage = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), uvcQW);
        List<UserVideoCompilation> records = userVideoCompilationIPage.getRecords();
        if (records.isEmpty()) {
            return PageData.emptyPage();
        }
        List<UserVideoCompilationVO> userVideoCompilationVOList = BeanCopyUtils.copyBeanList(records, UserVideoCompilationVO.class);
        // 封装VO
        packageUserVideoCompilationVOProcessor.processUserVideoCompilationVOList(userVideoCompilationVOList);
        return PageData.genPageData(userVideoCompilationVOList, userVideoCompilationIPage.getTotal());
    }

    /**
     * 合集播放量
     *
     * @param compilationId
     */
    @Override
    public Long compilationViewCount(Long compilationId) {
        return userVideoCompilationMapper.selectCompilationViewCount(compilationId);
    }

    /**
     * 获赞量
     *
     * @param compilationId
     */
    @Override
    public Long compilationLikeCount(Long compilationId) {
        return userVideoCompilationMapper.selectCompilationLikeCount(compilationId);
    }

    /**
     * 被收藏数
     *
     * @param compilationId
     */
    @Override
    public Long compilationFavoriteCount(Long compilationId) {
        // todo 收藏合集业务
//        return userVideoCompilationMapper.selectCompilationFavoriteCount(compilationId);
        return 0L;
    }

    /**
     * 视频数
     *
     * @param compilationId
     */
    @Override
    public Long compilationVideoCount(Long compilationId) {
        return userVideoCompilationMapper.selectCompilationVideoCount(compilationId);
    }

    /**
     * 更新视频合集
     *
     * @param updateUserVideoCompilationDTO
     */
    @Override
    public Boolean updateVideoCompilationInfo(UpdateUserVideoCompilationDTO updateUserVideoCompilationDTO) {
        Long compilationId = updateUserVideoCompilationDTO.getCompilationId();
        if (StringUtils.isNull(compilationId)) {
            return false;
        }
        LambdaUpdateWrapper<UserVideoCompilation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(StringUtils.isNotEmpty(updateUserVideoCompilationDTO.getTitle()), UserVideoCompilation::getTitle, updateUserVideoCompilationDTO.getTitle());
        updateWrapper.set(StringUtils.isNotEmpty(updateUserVideoCompilationDTO.getDescription()), UserVideoCompilation::getDescription, updateUserVideoCompilationDTO.getDescription());
        updateWrapper.set(StringUtils.isNotEmpty(updateUserVideoCompilationDTO.getCoverImage()), UserVideoCompilation::getCoverImage, updateUserVideoCompilationDTO.getCoverImage());
        updateWrapper.set(UserVideoCompilation::getUpdateTime, LocalDateTime.now());
        updateWrapper.eq(UserVideoCompilation::getCompilationId, compilationId).eq(UserVideoCompilation::getUserId, UserContext.getUserId());
        // todo 敏感词过滤
        // todo 审核
        return this.update(updateWrapper);
    }

    /**
     * 合集视频分页
     *
     * @param pageDTO
     */
    @Override
    public PageData compilationVideoPage(CompilationVideoPageDTO pageDTO) {
        List<CompilationVideoVO> vo = userVideoCompilationRelationService.compilationVideoPageList(pageDTO);
        return PageData.genPageData(vo, userVideoCompilationRelationService.compilationVideoPageCount(pageDTO.getCompilationId()));
    }

    /**
     * 根据视频id获取合集信息
     *
     * @param videoId
     * @return
     */
    @Override
    public UserVideoCompilationInfoVO getCompilationInfoVOByVideoId(String videoId) {
        Long compilationIdByVideoId = userVideoCompilationRelationService.getCompilationIdByVideoId(videoId);
        if (compilationIdByVideoId != null) {
            // 查询合集详情
            UserVideoCompilation byId = this.getById(compilationIdByVideoId);
            UserVideoCompilationInfoVO userVideoCompilationInfoVO = BeanCopyUtils.copyBean(byId, UserVideoCompilationInfoVO.class);
            userVideoCompilationInfoVO.setPlayCount(1000L);
            userVideoCompilationInfoVO.setVideoCount(20L);
            userVideoCompilationInfoVO.setWeatherFollow(false);
            // 查询视频数量 todo 让前端做
            return userVideoCompilationInfoVO;
        }
        return null;
    }
}
