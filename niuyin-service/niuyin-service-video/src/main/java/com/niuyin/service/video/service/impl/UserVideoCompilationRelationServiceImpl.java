package com.niuyin.service.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.video.domain.UserVideoCompilationRelation;
import com.niuyin.model.video.dto.CompilationVideoPageDTO;
import com.niuyin.model.video.vo.CompilationVideoVO;
import com.niuyin.service.video.mapper.UserVideoCompilationRelationMapper;
import com.niuyin.service.video.service.IUserVideoCompilationRelationService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户视频合集与视频关联表(UserVideoCompilationRelation)表服务实现类
 *
 * @author roydon
 * @since 2023-12-08 20:21:13
 */
@RequiredArgsConstructor
@Service
public class UserVideoCompilationRelationServiceImpl extends ServiceImpl<UserVideoCompilationRelationMapper, UserVideoCompilationRelation> implements IUserVideoCompilationRelationService {
    private final UserVideoCompilationRelationMapper userVideoCompilationRelationMapper;

    /**
     * 将视频添加到合集
     *
     * @param videoId
     * @param compilationId
     * @return
     */
    @Override
    public Boolean videoRelateCompilation(String videoId, Long compilationId) {
        UserVideoCompilationRelation userVideoCompilationRelation = new UserVideoCompilationRelation();
        userVideoCompilationRelation.setCompilationId(compilationId);
        userVideoCompilationRelation.setVideoId(videoId);
        return this.save(userVideoCompilationRelation);
    }

    /**
     * 删除视频
     *
     * @param videoId
     * @return
     */
    @Override
    public boolean deleteRecordByVideoId(String videoId) {
        LambdaQueryWrapper<UserVideoCompilationRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserVideoCompilationRelation::getVideoId, videoId);
        return this.remove(queryWrapper);
    }

    @Override
    public IPage<UserVideoCompilationRelation> compilationVideoPage(CompilationVideoPageDTO pageDTO) {
        if (StringUtils.isNull(pageDTO.getCompilationId())) {
            return new Page<>();
        }
        LambdaQueryWrapper<UserVideoCompilationRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserVideoCompilationRelation::getCompilationId, pageDTO.getCompilationId());
        queryWrapper.orderByDesc(UserVideoCompilationRelation::getCreateTime);
        return this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), queryWrapper);
    }

    /**
     * 合集视频分页查询
     *
     * @param pageDTO
     * @return
     */
    @Override
    public List<CompilationVideoVO> compilationVideoPageList(CompilationVideoPageDTO pageDTO) {
        pageDTO.setPageNum((pageDTO.getPageNum() - 1) * pageDTO.getPageSize());
        return userVideoCompilationRelationMapper.compilationVideoPageList(pageDTO);
    }

    @Override
    public Long compilationVideoPageCount(Long compilationId) {
        return userVideoCompilationRelationMapper.selectCompilationVideoPageCount(compilationId);
    }

    @Override
    public Long getCompilationIdByVideoId(String videoId) {
        LambdaQueryWrapper<UserVideoCompilationRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserVideoCompilationRelation::getVideoId, videoId);
        UserVideoCompilationRelation one = this.getOne(queryWrapper);
        return one == null ? null : one.getCompilationId();
    }
}
