package com.niuyin.model.ai.vo.model;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * ToolSimpleVO
 *
 * @AUTHOR: roydon
 * @DATE: 2025/6/5
 **/
@Data
public class ToolSimpleVO {
    /**
     * 工具编号
     */
    @TableId
    private Long id;
    /**
     * 工具名称
     */
    private String name;
    /**
     * 工具描述
     */
    private String description;
}
