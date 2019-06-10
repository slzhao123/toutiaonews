package com.myproject.toutiaonews.controller;

import com.myproject.toutiaonews.model.EntityType;
import com.myproject.toutiaonews.model.HostHolder;
import com.myproject.toutiaonews.model.News;
import com.myproject.toutiaonews.model.ViewObject;
import com.myproject.toutiaonews.service.LikeService;
import com.myproject.toutiaonews.service.NewsService;
import com.myproject.toutiaonews.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author slzhao
 * @create: 2019-06-04 16:19
 **/
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    UserService userService;

    @Autowired
    NewsService newsService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    private List<ViewObject> getNews(int userId, int offset, int limit) {
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        // ViewObject作为整体元素传递，ViewObject其实就是一个Map
        List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news); // key=news,value=news对象
            vo.set("user", userService.getUser(news.getUserId())); // 同一个news对象的userId

            if (0 != localUserId) {
                vo.set("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                vo.set("like", 0);
            }
            vos.add(vo); // 放入返回结果的list数组
        }
        return vos;
    }

    /**
     * 首页默认展示信息，这里默认展示10条信息
     * 可以获取到hostHolder，根据不同用户个性化展示首页
     */
    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "pop", defaultValue = "0") int pop) { // 将属性传入模板引擎
        model.addAttribute("vos", getNews(0, 0, 10)); // userId指定为0，说明显示所有用户发表的新闻
        if (hostHolder.getUser() != null) {
            pop = 0;
        }
        model.addAttribute("pop", pop);
        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId,
                            @RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("vos", getNews(userId, 0, 10)); // userId不等于0，显示当前选中用户发表的新闻
        model.addAttribute("pop", pop);
        return "home";
    }
}
