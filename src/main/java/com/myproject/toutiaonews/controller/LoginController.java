package com.myproject.toutiaonews.controller;

import com.myproject.toutiaonews.async.EventModel;
import com.myproject.toutiaonews.async.EventProducer;
import com.myproject.toutiaonews.async.EventType;
import com.myproject.toutiaonews.service.NewsService;
import com.myproject.toutiaonews.service.UserService;
import com.myproject.toutiaonews.utils.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author slzhao
 * @create: 2019-06-04 16:19
 **/
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "rember", defaultValue = "0") int rememberme,
                      HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.register(username, password);
            if (map.containsKey("ticket")) {  // 下发ticket后写入客户端Cookie
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");  // 设置全栈有效
                if (rememberme > 0) {
                    cookie.setMaxAge(5 * 24 * 3600); // 记住5天，当然也可以跟后台对应上
                }
                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0, "注册成功");
            } else {
                return ToutiaoUtil.getJSONString(1, map);
            }

            // TODO 用户名敏感词的过滤
        } catch (Exception e) {
            logger.error("注册异常", e.getMessage());
            return ToutiaoUtil.getJSONString(1, "注册异常");
        }
    }

    // @ResponseBody注解会返回一个字符串直接返回
    @RequestMapping(path = {"/login/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(Model model, @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rember", defaultValue = "0") int rememberme,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                eventProducer.fireEvent(new
                        EventModel(EventType.LOGIN)
                        .setActorId((int)map.get("userId"))
                        .setExt("username", username)
                        .setExt("email", "1324731774@qq.com"));
                return ToutiaoUtil.getJSONString(0, "登录成功");
            } else {
                return ToutiaoUtil.getJSONString(1, map);
            }

        } catch (Exception e) {
            logger.error("登录异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "登录异常");
        }
    }


    @RequestMapping(path = {"/logout/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {  // 从Cookie里面读取ticket
        userService.logout(ticket);
        return "redirect:/";
    }
}
