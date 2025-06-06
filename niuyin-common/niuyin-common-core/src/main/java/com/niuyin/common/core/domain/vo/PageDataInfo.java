package com.niuyin.common.core.domain.vo;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.niuyin.common.core.domain.R;
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
public class PageDataInfo<T> implements Serializable {
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
    private List<T> rows;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 是否还有更多数据，作为下拉加载更多使用
     */
    private Boolean hasMore;

    /**
     * 返回分页数据
     *
     * @param page 分页对象
     */
    public static <T> PageDataInfo<T> page(IPage<T> page) {
        if (CollUtil.isEmpty(page.getRecords())) {
            return emptyPage();
        }
        // 是否还有更多数据
        Boolean hasMore = page.getCurrent() < page.getPages();
        return new PageDataInfo<>(R.SUCCESS, "OK", page.getRecords(), page.getTotal(), hasMore);
    }

    /**
     * 返回分页数据
     */
    public static <T> PageDataInfo<T> genPageData(List<T> rows, long total) {
        return new PageDataInfo<>(R.SUCCESS, "OK", rows, total, true);
    }

    /**
     * 返回空数据
     */
    public static <T> PageDataInfo<T> emptyPage() {
        return new PageDataInfo<>(R.SUCCESS, "OK", null, 0, false);
    }

}
