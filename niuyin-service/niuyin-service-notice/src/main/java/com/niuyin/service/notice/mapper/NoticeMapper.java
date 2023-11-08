package com.niuyin.service.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.notice.domain.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知表(Notice)表数据库访问层
 *
 * @author roydon
 * @since 2023-11-08 16:21:44
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice>{

}

