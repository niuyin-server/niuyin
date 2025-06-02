package com.niuyin.model.ai;

import com.niuyin.model.common.dto.PageDTO;
import lombok.Data;

/**
 * ApiKeyPageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2025/5/31
 **/
@Data
public class ApiKeyPageDTO extends PageDTO {
    /**
     * 名称
     */
    private String name;
    /**
     * 平台
     */
    private String platform;
    /**
     * 状态[0正常1禁用]
     */
    private String stateFlag;
}
