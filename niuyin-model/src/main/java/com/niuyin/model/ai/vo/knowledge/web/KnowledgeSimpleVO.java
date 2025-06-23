package com.niuyin.model.ai.vo.knowledge.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class KnowledgeSimpleVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 编号
     */
    private Long id;
    /**
     * 知识库名称
     */
    private String name;
    /**
     * 封面图片
     */
    private String coverImg;

}

