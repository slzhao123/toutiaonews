package com.myproject.toutiaonews.service;

import com.myproject.toutiaonews.dao.CommentDAO;
import com.myproject.toutiaonews.model.Comment;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @Author slzhao
 * @create: 2019-06-08 16:07
 **/
@Service
public class CommentService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    CommentDAO commentDAO;

    public int addComment(Comment comment) {
        return commentDAO.addComment(comment);
    }

    public List<Comment> getCommentsByEntity(int entityId, int entityType) {
        return commentDAO.selectByEntity(entityId, entityType);
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public void deleteComment(int entityId, int entityType) {
        commentDAO.updateStatus(entityId, entityType, 1);
    }
}
