package com.niuyin.service.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.model.member.vo.MemberInfoVO;
import com.niuyin.service.member.mapper.MemberInfoMapper;
import com.niuyin.service.member.service.IMemberInfoService;
import com.niuyin.service.member.service.IMemberService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Resource
    private IMemberService memberService;

    /**
     * 查询我的信息详情
     *
     * @return
     */
    @Override
    public MemberInfoVO queryMemberInfo() {
        LambdaQueryWrapper<MemberInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MemberInfo::getUserId, UserContext.getUserId());
        MemberInfo memberInfo = getOne(queryWrapper);
        MemberInfoVO memberInfoVO = BeanCopyUtils.copyBean(memberInfo, MemberInfoVO.class);
        // 将用户信息封装
        Member member = memberService.queryById(UserContext.getUserId());
        memberInfoVO.setNickName(member.getNickName());
        memberInfoVO.setAvatar(member.getAvatar());
        memberInfoVO.setGender(member.getSex());
        memberInfoVO.setJoinTime(member.getCreateTime());
        return memberInfoVO;
    }

    /**
     * 上传背景图片
     *
     * @param file
     * @return
     */
    @Override
    public String uploadBackGround(MultipartFile file) {

        return "";
    }
}
