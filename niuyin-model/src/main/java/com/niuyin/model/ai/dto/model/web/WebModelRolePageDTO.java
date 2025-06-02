package com.niuyin.model.ai.dto.model.web;

import com.niuyin.model.common.dto.PageDTO;
import lombok.Data;

@Data
public class WebModelRolePageDTO extends PageDTO {
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色类别
     */
    private String category;
}

