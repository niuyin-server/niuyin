package com.niuyin.service.search.domain.vo;

import com.niuyin.service.search.domain.UserEO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * UserSearchVO
 *
 * @AUTHOR: roydon
 * @DATE: 2024/10/11
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserSearchVO extends UserEO {
    /**
     * 是否关注
     */
    private Boolean follow = false;

    /**
     * 获赞量
     */
    private Long likes = 0L;
    /**
     * 粉丝数
     */
    private Long fans = 0L;
}
