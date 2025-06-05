package com.niuyin.model.ai.domain.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.niuyin.model.common.BaseDO;
import lombok.Data;

import java.io.Serial;

/**
 * AI 工具表(Tool)实体类
 *
 * @author makejava
 * @since 2025-06-05 16:02:47
 */
@Data
@TableName("ai_tool")
public class ToolDO extends BaseDO {
    @Serial
    private static final long serialVersionUID = -28051785807774847L;
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
    /**
     * 状态
     */
    private String stateFlag;


}

