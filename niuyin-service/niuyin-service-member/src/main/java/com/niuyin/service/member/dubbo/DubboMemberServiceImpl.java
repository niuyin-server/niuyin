package com.niuyin.service.member.dubbo;

import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.service.member.service.IMemberService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 用户表(User)表服务接口
 *
 * @author roydon
 * @since 2023-10-24 19:18:25
 */
@DubboService
public class DubboMemberServiceImpl implements DubboMemberService {

    @Resource
    private IMemberService memberService;

    /**
     * 通过ID查询单条数据
     *
     * @param userId
     */
    @Override
    public Member apiGetById(Long userId) {
        return memberService.getById(userId);
    }

}
