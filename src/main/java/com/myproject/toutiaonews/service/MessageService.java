package com.myproject.toutiaonews.service;

import com.myproject.toutiaonews.dao.MessageDAO;
import com.myproject.toutiaonews.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author slzhao
 * @create: 2019-06-08 18:23
 **/
@Service
public class MessageService {

    @Autowired
    private MessageDAO messageDAO;

    public int addMessage(Message message) {
        return messageDAO.addMessage(message);
    }

    // 返回用户分组后的私信记录
    // 以conversationId分组，相同的conversationId只取最新的一条显示
    public List<Message> getConversationList(int userId, int offset, int limit) {
        // conversation的总条数存在id里
        return messageDAO.getConversationList(userId, offset, limit);
    }

    // 返回用户所有的私信记录
    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    public int getUnreadCount(int userId, String conversationId) {
        return messageDAO.getConversationUnReadCount(userId, conversationId);
    }
}
