package com.myproject.toutiaonews.async.handler;

import com.myproject.toutiaonews.async.EventHandler;
import com.myproject.toutiaonews.async.EventModel;
import com.myproject.toutiaonews.async.EventType;
import com.myproject.toutiaonews.model.Message;
import com.myproject.toutiaonews.model.User;
import com.myproject.toutiaonews.service.MessageService;
import com.myproject.toutiaonews.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author slzhao
 * @create: 2019-06-10 21:53
 **/
@Component
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandler(EventModel model) {
        Message message = new Message();
        User user = userService.getUser(model.getActorId());
        //message.setToId(model.getEntityOwnerId());
        message.setToId(model.getActorId());
        message.setContent("用户" + user.getName() + "赞了你的资讯,http:127.0.0.1:8080/news/"
                + String.valueOf(model.getEntityId()));
        message.setFromId(3); // 默认系统账号
        message.setConversationId(message.getFromId() < message.getToId() ? String.format("%d_%d", message.getFromId(), message.getToId()) :
                String.format("%d_%d", message.getToId(), message.getFromId()));
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {

        return Arrays.asList(EventType.LIKE);  // 只关心点赞
    }
}
