package com.myproject.toutiaonews.controller;

import com.myproject.toutiaonews.model.Comment;
import com.myproject.toutiaonews.model.EntityType;
import com.myproject.toutiaonews.model.HostHolder;
import com.myproject.toutiaonews.model.News;
import com.myproject.toutiaonews.model.ViewObject;
import com.myproject.toutiaonews.service.CommentService;
import com.myproject.toutiaonews.service.NewsService;
import com.myproject.toutiaonews.service.QiniuService;
import com.myproject.toutiaonews.service.UserService;
import com.myproject.toutiaonews.utils.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author slzhao
 * @create: 2019-06-06 10:57
 **/
@Controller
public class NewsController {

    @Autowired
    NewsService newsService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    HostHolder hostHolder;

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    // 向服务器请求展示图片
    @RequestMapping(path = {"/image"}, method = RequestMethod.GET)
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName, HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            // 客户端根据之前服务端下发的图片地址去读取图片
            // 服务端响应的图片的二进制流，到response的输出流
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR + imageName)), response.getOutputStream());
        } catch (Exception e) {
            logger.error("读取图片错误" + e.getMessage());
        }
    }

    // 上传图片
    @RequestMapping(path = {"/uploadImage/"}, method = { RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {  // 使用MultipartFile接受图片
        try {
            //String fileUrl = newsService.saveImage(file); // 上传图片到本地，测试专用
            String fileUrl = qiniuService.saveImage(file); // 上传图片到七牛云服务器
            if (fileUrl == null) {
                return ToutiaoUtil.getJSONString(1, "上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);

        } catch (Exception e) {
            logger.error("上传图片失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传失败");
        }
    }

    @RequestMapping(path = {"/user/addNews/"}, method = { RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link) {
        try {
            // TODO:资讯字段检测加强--链接、标题检测是否有效合法
            News news = new News();
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setImage(image);
            news.setLink(link);
            if (hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                // 设置一个匿名用户，或者说未登陆用户不让发布news
                news.setUserId(999);
            }
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("添加资讯失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "发布失败");
        }
    }

    // 展示资讯详情页，主要就是评论的展示
    @RequestMapping(path = {"/news/{newsId}"}, method = { RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model) {
        try {
            News news = newsService.getById(newsId);
            if (news != null) {
                List<Comment>  comments = commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
                List<ViewObject> commentVos = new ArrayList<>();
                for (Comment comment : comments) {
                    ViewObject vos = new ViewObject();
                    vos.set("comment", comment);
                    vos.set("user", userService.getUser(comment.getUserId()));
                    commentVos.add(vos);
                }
                model.addAttribute("comments", commentVos);
            }
            model.addAttribute("news", news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));  // 资讯作者
        } catch (Exception e) {
            logger.error("获取资讯明细错误" + e.getMessage());
        }
        return "detail";
    }

    // 增加评价，@ResponseBody表示返回json串
    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        try {
            // TODO：过滤content，敏感词
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);

            // 更新评论数量，以后可以异步实现
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(), count);
        } catch (Exception e) {
            logger.error("提交评论错误" + e.getMessage());
        }

        // 重定向，就是评论后自动刷新
        // TODO: 这里可以优化，用AJAX
        return "redirect:/news/" + String.valueOf(newsId);
    }
}
