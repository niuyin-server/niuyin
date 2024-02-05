package com.niuyin.model.notice.dto;

import com.niuyin.model.notice.domain.Notice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * NoticePageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/16
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class NoticePageDTO extends Notice {
    private Integer pageNum;
    private Integer pageSize;
}
