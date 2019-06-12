package com.myproject.toutiaonews.async.handler;

import com.myproject.toutiaonews.async.EventHandler;
import com.myproject.toutiaonews.async.EventModel;
import com.myproject.toutiaonews.async.EventType;
import com.myproject.toutiaonews.model.Message;
import com.myproject.toutiaonews.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author slzhao
 * @create: 2019-06-11 11:05
 **/
@Component
public class LoginExceptionHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Override
    public void doHandler(EventModel model) {
        // 判断是否有异常登录
        Message message = new Message();
        message.setToId(model.getActorId());
        message.setContent("你上次的登陆IP异常");
        // SYSTEM ACCOUNT
        message.setFromId(3);
        message.setCreatedDate(new Date());
        message.setConversationId(message.getFromId() < message.getToId() ? String.format("%d_%d", message.getFromId(), message.getToId()) :
                String.format("%d_%d", message.getToId(), message.getFromId()));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
