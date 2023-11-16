package com.niuyin.service.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.service.member.mapper.MemberInfoMapper;
import com.niuyin.service.member.service.IMemberInfoService;
import com.niuyin.service.member.service.IMemberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 会员详情表(MemberInfo)表服务实现类
 *
 * @author roydon
 * @since 2023-11-12 22:26:26
 */
@Service("memberInfoService")
public class MemberInfoServiceImpl extends ServiceImpl<MemberInfoMapper, MemberInfo> implements IMemberInfoService {
    @Resource
    private MemberInfoMapper memberInfoMapper;

    /**
     * 通过userId查询用户详情
     *
     * @param userId
     * @return
     */
    @Override
    public MemberInfo queryInfoByUserId(Long userId) {
        return memberInfoMapper.selectInfoByUserId(userId);
    }

}
