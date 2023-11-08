package com.niuyin.service.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.member.domain.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表(User)表数据库访问层
 *
 * @author roydon
 * @since 2023-10-24 19:18:24
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member>{
}

