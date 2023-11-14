package com.niuyin.model.member.vo;

import com.niuyin.model.member.domain.MemberInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * MemberInfoVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/12
 * 用户详情返回体
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberInfoVO extends MemberInfo {

    /**
     * 用户昵称
     */
    private String nickName;

    private String avatar;

    private String gender;

    // 加入时间
    private LocalDateTime joinTime;

}
