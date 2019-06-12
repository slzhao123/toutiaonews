package com.myproject.toutiaonews;

import com.myproject.toutiaonews.model.User;
import com.myproject.toutiaonews.utils.JedisAdapter;
import com.myproject.toutiaonews.utils.ToutiaoUtil;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 测试redis序列化方法
 *
 * @Author slzhao
 * @create: 2019-06-04 16:44
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaonewsApplication.class)
public class JedisTests {
    @Autowired
    JedisAdapter jedisAdapter;

    @Test
    public void testJedis() {
        jedisAdapter.set("hello", "world");
        Assert.assertEquals("world", jedisAdapter.get("hello"));
    }

    @Test
    public void testObject() {
        User user = new User();
        user.setHeadUrl("http://images.nowcoder.com/head/100t.png");
        user.setName("user1");
        user.setPassword("abc");
        user.setSalt("defjlf");
        jedisAdapter.setObject("user1", user); // 序列化

        // 反序列化，先从redis中取出key对应的json文本，然后指定其属于哪个类
        User u = jedisAdapter.getObject("user1", User.class);
        System.out.println(ToStringBuilder.reflectionToString(u)); // 打印对象所有信息
    }

}
