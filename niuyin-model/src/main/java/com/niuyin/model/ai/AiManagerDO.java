package com.niuyin.model.ai;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.niuyin.model.common.BaseDO;
import lombok.Data;

import java.io.Serial;

/**
 * AI管理员表(AiManager)实体类
 *
 * @author roydon
 * @since 2025-05-30 23:39:16
 */
@Data
@TableName("ai_manager")
public class AiManagerDO extends BaseDO {
    @Serial
    private static final long serialVersionUID = -17259521472001941L;
    /**
     * 管理员编号
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 备注
     */
    private String remark;


}

