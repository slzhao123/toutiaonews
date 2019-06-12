package com.myproject.toutiaonews.async;

import com.alibaba.fastjson.JSON;
import com.myproject.toutiaonews.utils.JedisAdapter;
import com.myproject.toutiaonews.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author slzhao
 * @create: 2019-06-10 22:13
 **/
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();  // 映射表
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    // 1.生成映射表，key=eventType，value=List[handler1,handler2,...]
    // 2.消费
    @Override
    public void afterPropertiesSet() throws Exception {
        // 寻找所有实现了eventHandler的实现类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();  // 感兴趣的事件
                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) { // 如果该eventType列表不存在，则初始化
                        config.put(type, new ArrayList<>());
                    }

                    // 注册对eventType感兴趣的handler
                    config.get(type).add(entry.getValue());
                }
            }
        }

        // 启动线程消费事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 从队列取出消费 FIFO
                while (true) {
                    String key = RedisKeyUtil.getEventQueueKey(); // 指定队列名
                    List<String> messages = jedisAdapter.brpop(0, key); // 从key队列里一直阻塞地取event
                    // 因为brpop的第一个返回值为key，第二个才是我们需要的value值
                    for (String message : messages) {
                        if (message.equals(key)) {
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        // 找到这个eventType的关注列表
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("无法识别的事件");
                            continue;
                        }

                        for (EventHandler eventHandler : config.get(eventModel.getType())) {
                            eventHandler.doHandler(eventModel);
                        }
                    }

                }
            }
        });
        thread.start();
    }

    // 用来识别所有实现了eventHandler接口的handler
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
