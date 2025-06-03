package com.niuyin.model.ai.domain.chat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.niuyin.model.ai.domain.knowledge.KnowledgeSegmentDO;
import com.niuyin.model.ai.domain.model.ChatModelDO;
import com.niuyin.model.ai.domain.model.ModelRoleDO;
import com.niuyin.model.common.BaseDO;
import com.niuyin.model.common.handler.LongListTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * AI 聊天消息表(ai_chat_message)实体类
 *
 * @author roydon
 * @since 2025-04-22 15:39:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("ai_chat_message")
public class ChatMessageDO extends BaseDO {
    @Serial
    private static final long serialVersionUID = 262502003074283660L;
    /**
     * 消息编号
     */
    @TableId
    private Long id;
    /**
     * 对话编号
     */
    private Long conversationId;
    /**
     * 回复编号
     */
    private Long replyId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 对话类型
     */
    private String messageType;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 是否携带上下文[0否1是]
     */
    private String useContext;

    /**
     * 角色编号
     *
     * 关联 {@link ModelRoleDO#getId()} 字段
     */
    private Long roleId;

    /**
     * 模型标志
     *
     * 冗余 {@link ChatModelDO#getModel()}
     */
    private String model;
    /**
     * 模型编号
     *
     * 关联 {@link ChatModelDO#getId()} 字段
     */
    private Long modelId;
    /**
     * 知识库段落编号数组
     *
     * 关联 {@link KnowledgeSegmentDO#getId()} 字段
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> segmentIds;

}
