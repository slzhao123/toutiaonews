package com.myproject.toutiaonews.service;

import com.alibaba.fastjson.JSONObject;
import com.myproject.toutiaonews.utils.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @Author slzhao
 * @create: 2019-06-06 15:39
 **/
@Service
public class QiniuService {

    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);

    // 首先要申请一个七牛云账号
    // 设置好账号的ACCESS_KEY和SECRET_KEY
    String ACCESS_KEY = "EWPoX6zGtvxEHiwzSL2KyyflCnVZ-NTvhtTcvgLU";
    String SECRET_KEY = "rfcagkeBuhsxLLQID1ui6eYDaqS7vncV-JFp0Ffq";
    // 要上传的空间
    String bucketname = "bucket_ray";
    // 密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    // 创建要上传的对象
    UploadManager uploadManager = new UploadManager();

    private static String QINIU_IMAGE_DOMAIN = "http://pst94snkp.bkt.clouddn.com/";  // 展示图片用固定域名

    // 简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        return auth.uploadToken(bucketname);
    }

    public String saveImage(MultipartFile file) throws IOException {
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if (!ToutiaoUtil.isFileAllowed(fileExt)) {
                return null;
            }
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            // 调用put方法上传
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken());
            // 打印返回的信息，调试专用
            System.out.println(res.bodyString());
            if (res.isOK() && res.isJson()) {
                return QINIU_IMAGE_DOMAIN + JSONObject.parseObject(res.bodyString()).get("key");
            } else {
                logger.error("七牛异常：" + res.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            // 请求失败时打印的异常信息
            logger.error("七牛异常：" + e.getMessage());
            return null;
        }

    }
}
