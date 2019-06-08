package com.myproject.toutiaonews.model;

import org.springframework.stereotype.Component;

/**
 * @Author slzhao
 * @create: 2019-06-05 22:08
 * 用于全局保存验证成功后的用户信息
 **/
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<User>(); // 可能保存了多个用户的信息

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();;
    }
}
