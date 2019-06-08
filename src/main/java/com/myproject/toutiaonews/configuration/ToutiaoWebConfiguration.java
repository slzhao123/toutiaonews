package com.myproject.toutiaonews.configuration;

import com.myproject.toutiaonews.interceptor.LoginRequiredInterceptor;
import com.myproject.toutiaonews.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * @Author slzhao
 * @create: 2019-06-05 22:21
 **/
@Component
public class ToutiaoWebConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);   // 拦截所有页面，判断登录用户，注意拦截顺序
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/setting*");  // 拦截某些权限页面
        super.addInterceptors(registry);
    }
}
