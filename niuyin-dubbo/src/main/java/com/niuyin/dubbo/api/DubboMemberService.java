package com.niuyin.dubbo.api;

import com.niuyin.model.member.domain.Member;

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

}
