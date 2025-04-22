package com.niuyin.model.common;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * BaseDO
 *
 * @AUTHOR: roydon
 * @DATE: 2025/4/22
 **/
@Data
public class BaseDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    /**
     * 删除标记[0正常1删除]
     */
    @TableLogic(value = "0", delval = "1")
    private String delFlag;
}
