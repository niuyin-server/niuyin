package com.niuyin.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.video.domain.UserVideoCompilation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 用户视频合集表(UserVideoCompilation)表数据库访问层
 *
 * @author roydon
 * @since 2023-11-27 18:08:38
 */
@Mapper
public interface UserVideoCompilationMapper extends BaseMapper<UserVideoCompilation> {

    /**
     * 合集播放量
     *
     * @param compilationId
     * @return
     */
    Long selectCompilationViewCount(@Param("compilationId") Long compilationId);

    /**
     * 合集获赞量
     *
     * @param compilationId
     * @return
     */
    Long selectCompilationLikeCount(@Param("compilationId") Long compilationId);

    /**
     * 合集视频数
     *
     * @param compilationId
     * @return
     */
    Long selectCompilationVideoCount(@Param("compilationId") Long compilationId);
}

