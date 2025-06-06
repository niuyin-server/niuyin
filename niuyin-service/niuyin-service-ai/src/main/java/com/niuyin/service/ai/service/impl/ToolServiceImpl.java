package com.niuyin.service.ai.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.bean.BeanUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.ai.domain.model.ModelRoleDO;
import com.niuyin.model.ai.domain.model.ToolDO;
import com.niuyin.model.ai.dto.model.AiToolSaveDTO;
import com.niuyin.model.ai.dto.model.ToolPageDTO;
import com.niuyin.model.common.enums.StateFlagEnum;
import com.niuyin.service.ai.controller.admin.AdminToolController;
import com.niuyin.service.ai.mapper.ToolMapper;
import com.niuyin.service.ai.service.IToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 工具表(Tool)表服务实现类
 *
 * @author makejava
 * @since 2025-06-05 16:02:48
 */
@RequiredArgsConstructor
@Service
public class ToolServiceImpl extends ServiceImpl<ToolMapper, ToolDO> implements IToolService {
    private final ToolMapper toolMapper;
    private final SnowFlake snowFlake;

    @Override
    public Long createTool(AiToolSaveDTO dto) {
        // 校验名称是否存在
        validateToolNameExists(dto.getName());

        // 插入
        ToolDO tool = BeanUtils.toBean(dto, ToolDO.class);
        tool.setId(snowFlake.nextId());
        toolMapper.insert(tool);
        return tool.getId();
    }

    @Override
    public void updateTool(AiToolSaveDTO dto) {
        // 1.2 校验名称是否存在
        validateToolNameExists(dto.getName());

        // 2. 更新
        ToolDO updateObj = BeanUtils.toBean(dto, ToolDO.class);
        toolMapper.updateById(updateObj);
    }

    private void validateToolNameExists(String name) {
        try {
            SpringUtil.getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            throw new RuntimeException("名称已存在");
        }
    }

    @Override
    public void deleteTool(Long id) {
        toolMapper.deleteById(id);
    }

    @Override
    public ToolDO getTool(Long id) {
        return toolMapper.selectById(id);
    }

    @Override
    public PageDataInfo<ToolDO> getToolPage(ToolPageDTO pageDTO) {
        LambdaQueryWrapper<ToolDO> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(pageDTO.getName()), ToolDO::getName, pageDTO.getName())
                .eq(ToolDO::getStateFlag, StateFlagEnum.ENABLE.getCode());
        Page<ToolDO> page = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), qw);
        return PageDataInfo.page(page);
    }

    @Override
    public List<ToolDO> getToolListByState(String state) {
        return toolMapper.selectList(new LambdaQueryWrapper<ToolDO>().eq(ToolDO::getStateFlag, state));
    }

    @Override
    public void updateToolState(AdminToolController.ToolStateDTO dto) {
        ToolDO toolDO = BeanCopyUtils.copyBean(dto, ToolDO.class);
        toolMapper.updateById(toolDO);
    }
}
