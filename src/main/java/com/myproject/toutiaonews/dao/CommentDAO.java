package com.myproject.toutiaonews.dao;

import com.myproject.toutiaonews.model.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, content, created_date, entity_id, entity_type, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId}, #{content}, #{createdDate}, #{entityId}, #{entityType}, #{status})"})
    int addComment(Comment comment);

    // TODO: 分页显示，加上offset、limit字段即可
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where entity_type = #{entityType} and entity_id = #{entityId} order by id desc"})
    List<Comment> selectByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select count(id) from ", TABLE_NAME, " where entity_type = #{entityType} and entity_id = #{entityId}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Update({"update ", TABLE_NAME, " set status = #{status} where entity_type = #{entityType} and entity_id = #{entityId}"})
    void updateStatus(@Param("entityId") int entityId, @Param("entityType") int entityType, @Param("status") int status);
}
