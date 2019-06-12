package com.myproject.toutiaonews.service;

import com.myproject.toutiaonews.dao.LoginTicketDAO;
import com.myproject.toutiaonews.dao.UserDAO;
import com.myproject.toutiaonews.model.LoginTicket;
import com.myproject.toutiaonews.model.User;
import com.myproject.toutiaonews.utils.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * @Author slzhao
 * @create: 2019-06-04 16:07
 **/
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if (user != null) {
            map.put("msgname", "用户名已经被注册");
            return map;
        }

        // 密码强度
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5)); // 截取随机值前5位
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(ToutiaoUtil.MD5(password + user.getSalt()));
        userDAO.addUser(user);

        // 登录，使用一个ticket来记录登录状态，就是一个cookie
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if (user == null) {
            map.put("msgname", "用户名不存在，请先注册。");
            return map;
        }

        // MD5是生成消息摘要，并不是加密，相同明文得到的MD5值相同
        if (!ToutiaoUtil.MD5(password + user.getSalt()).equals(user.getPassword())) {
            map.put("msgpwd", "密码不正确");
            return map;
        }

        map.put("userId", user.getId());

        // 给用户下发一个ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    /**
     * 针对不同用户生成一个唯一的ticket字符串
     */
    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 24 * 3600 * 1000); // 一天后过期
        ticket.setExpired(date);
        ticket.setStatus(0); // ticket有效
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", "")); // UUID中间带有"-"
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1); // 设置ticket无效
    }
}
