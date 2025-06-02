package com.niuyin.model.ai.dto.model;

import com.niuyin.model.common.dto.PageDTO;
import lombok.Data;

/**
 * ModelRolePageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2025/6/2
 **/
@Data
public class ModelRolePageDTO extends PageDTO {
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色类别
     */
    private String category;
    /**
     * 是否公开[0私有1公开]
     */
    private String publicFlag;
    /**
     * 状态
     */
    private String stateFlag;
}

