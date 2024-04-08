package com.niuyin.model.social.dto;

import com.niuyin.model.common.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 社交动态分页dto
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/7
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class SocialDynamicsPageDTO extends PageDTO {
    private Long userId;
}
