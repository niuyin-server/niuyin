package com.niuyin.dubbo.api;

import com.niuyin.model.member.domain.Member;

import java.util.List;

/**
 * dubbo 服务提供者： niuyin-member
 *
 * @author roydon
 * @since 2023-10-24 19:18:25
 */
public interface DubboMemberService {

    /**
     * 通过ID查询单条数据
     */
    Member apiGetById(Long userId);

    /**
     * 通过ID查询头像
     */
    String apiGetAvatarById(Long userId);

    /**
     * 根据ids查询
     */
    List<Member> apiGetInIds(List<Long> userIds);

}
