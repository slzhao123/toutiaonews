package com.myproject.toutiaonews.model;

import java.util.Date;

/**
 * @Author slzhao
 * @create: 2019-06-05 20:12
 **/
public class LoginTicket {

    private int id;
    private int userId;
    private Date expired;  // ticket过期时间
    private int status;    // 0有效；1无效
    private String ticket; // 生成的ticket字符串

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
