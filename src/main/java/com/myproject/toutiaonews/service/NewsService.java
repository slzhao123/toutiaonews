package com.myproject.toutiaonews.service;

import com.myproject.toutiaonews.dao.NewsDAO;
import com.myproject.toutiaonews.model.News;
import com.myproject.toutiaonews.utils.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * @Author slzhao
 * @create: 2019-06-04 16:09
 **/
@Service
public class NewsService {

    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }

    public News getById(int newsId) {
        return newsDAO.getById(newsId);
    }

    public int addNews(News news) {
        newsDAO.addNews(news);
        return news.getId();
    }

    public String saveImage(MultipartFile file) throws Exception {
        // 判断该文件是否是一张图片，严格的判断是需要引用图片库的
        // 这里仅仅进行简易的判断，仅仅判断后缀名
        // TODO 判断文件是否是一张图片，判断图片是否已经上传过
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        // 获取小写的文件扩展名
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if (!ToutiaoUtil.isFileAllowed(fileExt)) { // 检测图片扩展名是否合法
            return null;
        }

        // 测试专用，先将图片上传到本地文件目录
        // 替换原图片名为随机生成的字符串
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        // 返回图片路径地址链接便于前端访问
        // 将生成的图片路径下发给客户端
        return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;
    }

    public int updateCommentCount(int id, int count) {
        return newsDAO.updateCommentCount(id, count);
    }

    public int updateLikeCount(int id, int count) {
        return newsDAO.updateLikeCount(id, count);
    }
}
