package com.niuyin.model.ai.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.niuyin.model.common.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

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

}
