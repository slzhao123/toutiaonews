package com.myproject.toutiaonews.service;

import com.myproject.toutiaonews.ToutiaonewsApplication;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaonewsApplication.class)
public class LikeServiceTest {

    @Autowired
    LikeService likeService;

    @Test
    public void testLike() {
        likeService.like(123, 1, 1);
        Assert.assertEquals(1, likeService.getLikeStatus(123, 1, 1));

        likeService.disLike(123, 1, 1);
        Assert.assertEquals(-1, likeService.getLikeStatus(123, 1, 1));
    }

    @Test
    public void testB() {
        System.out.println("B");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        throw new IllegalArgumentException("异常");
    }

    @Before
    public void setUp() {
        System.out.println("setUp");
    }

    @After
    public void tearDown() {
        System.out.println("tearDown");
    }

    @BeforeClass
    public static void beforeClass() {  // 只跑一次，类初始化用
        System.out.println("BeforeClass");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("afterClass");
    }

}
