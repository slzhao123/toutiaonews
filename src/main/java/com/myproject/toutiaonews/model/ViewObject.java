package com.myproject.toutiaonews.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author slzhao
 * @create: 2019-06-04 16:20
 * 内置的一个Map
 **/
public class ViewObject {

    private Map<String, Object> objs = new HashMap<>();

    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }

}
