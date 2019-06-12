package com.myproject.toutiaonews.async;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author slzhao
 * @create: 2019-06-10 21:26
 **/
public class EventModel {
    private EventType type;  // 事件类型，比如评论，邮件，日志等
    private int actorId;    // 事件触发者
    private int entityId;
    private int entityType;
    private int entityOwnerId;  // 触发的对象拥有者，比如对于某条评论属于哪个用户
    private Map<String, String> exts = new HashMap<>(); // exts:扩展信息，保存现场参数信息


    public String getExt(String key) {
        return exts.get(key);
    }

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;  // 便于获取现场参数信息
    }

    public EventModel() {
    }

    public EventModel(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public void setExts(Map<String, String> exts) {
        this.exts = exts;
    }
}
