package com.niuyin.service.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.member.domain.MemberInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员详情表(MemberInfo)表数据库访问层
 *
 * @author roydon
 * @since 2023-11-12 22:26:25
 */
@Mapper
public interface MemberInfoMapper extends BaseMapper<MemberInfo>{

}
