package com.limon.barkod.limonbarkod.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultModel {

    private List<ResultItem> item;

    public ResultModel() {
        this.item = new ArrayList<>();
    }

    public List<ResultItem> getItem() {
        return item;
    }
    public void setItem(List<ResultItem> item) {
        this.item = item;
    }

}
