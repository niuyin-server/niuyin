package com.niuyin.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.video.domain.UserVideoCompilation;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 用户视频合集表(UserVideoCompilation)表数据库访问层
 *
 * @author roydon
 * @since 2023-11-27 18:08:38
 */
public interface UserVideoCompilationMapper extends BaseMapper<UserVideoCompilation> {

    /**
     * 查询指定行数据
     *
     * @param userVideoCompilation 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<UserVideoCompilation> queryAllByLimit(UserVideoCompilation userVideoCompilation, @Param("pageable") Pageable pageable);

}

