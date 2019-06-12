package com.myproject.toutiaonews.async;

/** 根据业务枚举定义不同的event类型
 * @Author slzhao
 * @create: 2019-06-10 21:27
 **/
public enum  EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);

    private int value;
    EventType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
