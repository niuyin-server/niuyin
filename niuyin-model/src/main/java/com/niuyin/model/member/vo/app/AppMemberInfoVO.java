package com.niuyin.model.member.vo.app;

import com.niuyin.model.member.domain.Member;
import com.niuyin.model.member.domain.MemberInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MemberInfoVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/12
 * 用户详情返回体
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class AppMemberInfoVO extends Member {

    // 用户详情
    private MemberInfo memberInfo;

    // 是否关注
    private Boolean weatherFollow;

    // 获赞
    private Long likeCount;

    // 关注
    private Long followCount;

    // 粉丝
    private Long fansCount;

}
