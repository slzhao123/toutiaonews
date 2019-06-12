package com.myproject.toutiaonews.controller;

import com.myproject.toutiaonews.async.EventModel;
import com.myproject.toutiaonews.async.EventProducer;
import com.myproject.toutiaonews.async.EventType;
import com.myproject.toutiaonews.model.EntityType;
import com.myproject.toutiaonews.model.HostHolder;
import com.myproject.toutiaonews.model.News;
import com.myproject.toutiaonews.service.LikeService;
import com.myproject.toutiaonews.service.NewsService;
import com.myproject.toutiaonews.utils.ToutiaoUtil;
import org.apache.catalina.Host;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author slzhao
 * @create: 2019-06-10 19:10
 **/
@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@Param("newsId") int newsId) {
        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
        // 更新喜欢数
        News news = newsService.getById(newsId);
        newsService.updateLikeCount(newsId, (int)likeCount);

        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(newsId)
                .setEntityType(EntityType.ENTITY_NEWS)
                .setEntityOwnerId(news.getUserId()));


        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@Param("newsId") int newsId) {
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
        // 更新喜欢数
        // News news = newsService.getById(newsId);
        newsService.updateLikeCount(newsId, (int)likeCount);
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
