package com.niuyin.model.ai.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * ModelRoleSaveDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2025/6/2
 **/
@Data
public class ModelRoleSaveDTO {

    private Long id;
    /**
     * 模型编号
     */
    @NotNull(message = "模型编号不能为空")
    private Long modelId;
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
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
    @NotBlank(message = "角色上下文不能为空")
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

    @Schema(description = "引用的知识库编号列表", example = "1,2,3")
    private List<Long> knowledgeIds;

    @Schema(description = "引用的工具编号列表", example = "1,2,3")
    private List<Long> toolIds;

}
