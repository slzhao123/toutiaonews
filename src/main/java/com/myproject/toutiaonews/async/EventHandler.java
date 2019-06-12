package com.myproject.toutiaonews.async;

import java.util.List;

public interface EventHandler {
    void doHandler(EventModel model);  // 不同的handler处理事件的步骤不同，抽成一个方法
    List<EventType> getSupportEventTypes();  // 这个handler需要关注的event列表，各个handler只关注自己感兴趣的event
}
