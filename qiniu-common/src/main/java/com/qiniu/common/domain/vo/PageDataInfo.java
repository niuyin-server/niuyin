
package com.qiniu.common.domain.vo;

import com.qiniu.common.domain.R;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页数据对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDataInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息状态码
     */
    private int code;

    /**
     * 消息内容
     */
    private String msg;

    /**
     * 列表数据
     */
    private List<?> rows;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 返回分页数据
     *
     * @param rows
     * @param total
     * @return
     */
    public static PageDataInfo genPageData(List<?> rows, long total) {
        return new PageDataInfo(R.SUCCESS, "OK", rows, total);
    }

}
