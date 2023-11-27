package com.niuyin.service.member.dubbo.v101;

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
 * 超时与重试，预防网络抖动，version灰度发布，默认负载均衡random，可指定weight
 */
@DubboService(timeout = 2000, retries = 4, version = "1.0.1", weight = 10)
public class DubboMemberServiceImplV101 implements DubboMemberService {

    @Resource
    private IMemberService memberService;

    /**
     * 通过ID查询单条数据
     *
     * @param userId
     */
    @Override
    public Member apiGetById(Long userId) {
        Member byId = memberService.getById(userId);
        byId.setNickName("1.0.1");
        return byId;
    }

}
