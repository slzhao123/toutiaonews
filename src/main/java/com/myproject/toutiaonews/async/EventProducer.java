package com.myproject.toutiaonews.async;

import com.alibaba.fastjson.JSONObject;
import com.myproject.toutiaonews.utils.JedisAdapter;
import com.myproject.toutiaonews.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author slzhao
 * @create: 2019-06-10 21:58
 **/
@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel) {
        try {
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
