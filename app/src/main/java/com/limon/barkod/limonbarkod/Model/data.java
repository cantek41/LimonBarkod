package com.limon.barkod.limonbarkod.Model;

import java.util.HashMap;
import java.util.Map;

public class data {

    private Map<String, Object> d;

    public data() {
        this.d = new HashMap<String, Object>();
    }

    public Map<String, Object> getd() {
        return d;
    }

    public void setd(Map<String, Object> d) {
        this.d = d;
    }
}
