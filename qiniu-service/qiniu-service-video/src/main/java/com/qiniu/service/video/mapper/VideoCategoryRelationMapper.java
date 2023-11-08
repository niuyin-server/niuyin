package com.qiniu.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiniu.model.video.domain.VideoCategoryRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 视频分类关联表(VideoCategoryRelation)表数据库访问层
 *
 * @author lzq
 * @since 2023-10-31 14:44:34
 */
@Mapper
public interface VideoCategoryRelationMapper extends BaseMapper<VideoCategoryRelation>{



}

