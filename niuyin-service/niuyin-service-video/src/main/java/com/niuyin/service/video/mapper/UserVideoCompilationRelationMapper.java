package com.niuyin.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.video.domain.UserVideoCompilationRelation;
import com.niuyin.model.video.dto.CompilationVideoPageDTO;
import com.niuyin.model.video.vo.CompilationVideoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户视频合集与视频关联表(UserVideoCompilationRelation)表数据库访问层
 *
 * @author roydon
 * @since 2023-12-08 20:21:12
 */
@Mapper
public interface UserVideoCompilationRelationMapper extends BaseMapper<UserVideoCompilationRelation>{

    /**
     * 合集视频分页
     * @param pageDTO
     * @return
     */
    List<CompilationVideoVO> compilationVideoPageList(CompilationVideoPageDTO pageDTO);

    /**
     * 合集视频数量
     */
    Long selectCompilationVideoPageCount(Long compilationId);
}

