package com.niuyin.service.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.model.member.vo.app.AppMemberInfoVO;

/**
 * 会员详情表(MemberInfo)表服务接口
 *
 * @author roydon
 * @since 2023-11-12 22:26:25
 */
public interface IMemberInfoService extends IService<MemberInfo> {

    /**
     * 通过userId查询用户详情
     *
     * @param userId
     * @return
     */
    MemberInfo queryInfoByUserId(Long userId);

    AppMemberInfoVO getUserInfoById(Long userId);

}
