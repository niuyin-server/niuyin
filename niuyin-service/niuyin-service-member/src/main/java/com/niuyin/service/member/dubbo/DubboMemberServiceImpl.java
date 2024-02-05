package com.niuyin.service.member.dubbo;

import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.service.member.service.IMemberService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author roydon
 * @since 2023-10-24 19:18:25
 * 超时与重试，预防网络抖动
 * mock=true实现服务降级
 */
@DubboService(timeout = 2000, retries = 4, weight = 1)
public class DubboMemberServiceImpl implements DubboMemberService {

    @Resource
    private IMemberService memberService;

    /**
     * 通过ID查询单条数据
     */
    @Override
    public Member apiGetById(Long userId) {
        return memberService.getById(userId);
    }

    /**
     * 通过ID查询头像
     *
     * @param userId
     */
    @Override
    public String apiGetAvatarById(Long userId) {
        return memberService.getAvatarById(userId);
    }

    /**
     * 根据ids查询
     */
    @Override
    public List<Member> apiGetInIds(List<Long> userIds) {
        return memberService.queryInIds(userIds);
    }
}
