package com.myproject.toutiaonews.service;

import com.myproject.toutiaonews.utils.JedisAdapter;
import com.myproject.toutiaonews.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

/** 某个用户对某个元素是否喜欢，使用entityId&entityType区分不同事物
 * @Author slzhao
 * @create: 2019-06-10 16:43
 **/
@Service
public class LikeService {

    @Autowired
    JedisAdapter jedisAdapter;

    // 判断某个用户对某一项元素是否喜欢
    // 1喜欢；-1不喜欢；0初始
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    // 点赞
    public long like(int userId, int entityType, int entityId) {
        // 在喜欢的集合里增加
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        // 在不喜欢里删除，如果存在的话
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);  // 返回喜欢集合的人数
    }

    // 点踩
    public long disLike(int userId, int entityType, int entityId) {
        // 在不喜欢的集合里增加
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));

        // 在喜欢的集合里删除，如果存在的话
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);  // 仍旧返回喜欢集合的人数，因为默认都是现实点赞人数
    }
}
