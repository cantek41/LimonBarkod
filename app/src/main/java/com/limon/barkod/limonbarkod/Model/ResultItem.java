package com.limon.barkod.limonbarkod.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultItem extends ResultModel {
    private int Id;
    private int Order;
    private String Title;
    private String Type;
    private String ColWidth;
    private ArrayList<Map<String, Object>> Data;

    public ResultItem() {
//        Data = new ArrayList<>();
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getColWidth() {
        return ColWidth;
    }

    public void setColWidth(String colWidth) {
        ColWidth = colWidth;
    }


    public ArrayList<Map<String, Object>> getData() {
        return Data;
    }

    public void setData(ArrayList<Map<String, Object>> data) {
        Data = data;
    }
}
