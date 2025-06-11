package com.niuyin.model.ai.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.niuyin.model.ai.domain.knowledge.KnowledgeDO;
import com.niuyin.model.common.BaseDO;
import com.niuyin.model.common.handler.LongListTypeHandler;
import lombok.Data;

import java.io.Serial;
import java.util.List;

/**
 * AI 聊天角色表(ModelRole)实体类
 *
 * @author roydon
 * @since 2025-06-02 15:30:42
 */
@Data
@TableName("ai_model_role")
public class ModelRoleDO extends BaseDO {
    @Serial
    private static final long serialVersionUID = 197421509258722922L;
    /**
     * 角色编号
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 模型编号
     */
    private Long modelId;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 角色类别
     */
    private String category;
    /**
     * 角色描述
     */
    private String description;
    /**
     * 对话开场白
     */
    private String chatPrologue;
    /**
     * 角色上下文
     */
    private String systemMessage;
    /**
     * 是否公开[0私有1公开]
     */
    private String publicFlag;
    /**
     * 状态
     */
    private String stateFlag;
    /**
     * 角色排序
     */
    private Integer sort;
    /**
     * 引用的知识库编号列表
     * <p>
     * 关联 {@link KnowledgeDO#getId()} 字段
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> knowledgeIds;
    /**
     * 引用的工具编号列表
     * <p>
     * 关联 {@link ToolDO#getId()} 字段
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> toolIds;


}

