package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.ai.domain.model.ToolDO;
import com.niuyin.model.ai.dto.model.AiToolSaveDTO;
import com.niuyin.model.ai.dto.model.ToolPageDTO;
import com.niuyin.service.ai.controller.admin.AdminToolController;

import java.util.List;

/**
 * AI 工具表(Tool)表服务接口
 *
 * @author makejava
 * @since 2025-06-05 16:02:47
 */
public interface IToolService extends IService<ToolDO> {

    Long createTool(AiToolSaveDTO dto);

    void updateTool(AiToolSaveDTO dto);

    void deleteTool(Long id);

    ToolDO getTool(Long id);

    PageDataInfo<ToolDO> getToolPage(ToolPageDTO pageDTO);

    List<ToolDO> getToolListByState(String state);

    void updateToolState(AdminToolController.ToolStateDTO dto);
}
