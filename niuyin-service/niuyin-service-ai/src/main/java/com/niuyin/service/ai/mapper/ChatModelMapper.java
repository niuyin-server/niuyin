package com.niuyin.service.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.ai.domain.model.ChatModelDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * AI 聊天模型表(AiChatModel)表数据库访问层
 *
 * @author roydon
 * @since 2025-06-02 13:41:27
 */
@Mapper
public interface ChatModelMapper extends BaseMapper<ChatModelDO>{


}

