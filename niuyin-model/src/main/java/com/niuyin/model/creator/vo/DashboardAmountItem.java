package com.niuyin.model.creator.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DashboardAmountItem
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/11
 **/
@Data
@AllArgsConstructor
public class DashboardAmountItem {
    private Long now;
    private Long add;
    private List<Long> gather;

    public DashboardAmountItem(Long now, Long add) {
        this.now = now;
        this.add = add;
        this.gather = new ArrayList<>(Collections.nCopies(7, 0L));
    }
}
