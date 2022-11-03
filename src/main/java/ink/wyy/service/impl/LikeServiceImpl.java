package ink.wyy.service.impl;

import ink.wyy.bean.*;
import ink.wyy.mapper.LikeMapper;
import ink.wyy.service.ArticleService;
import ink.wyy.service.LikeService;
import ink.wyy.service.NotificationService;
import ink.wyy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 点赞服务
 */

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ArticleService articleService;

    @Autowired
    public LikeServiceImpl(LikeMapper likeMapper, NotificationService notificationService, UserService userService, ArticleService articleService) {
        this.likeMapper = likeMapper;
        this.notificationService = notificationService;
        this.userService = userService;
        this.articleService = articleService;
    }

    /**
     * 获取用户点赞的文章id
     * @param userId
     * @param pager
     * @return
     */
    @Override
    public Pager<String> getListByUser(String userId, Pager<String> pager) {
        List<String> list = likeMapper.getListByUser(userId, pager);
        if (list == null) {
            return null;
        }
        pager.setRows(list);
        pager.setTotal(likeMapper.countByUserId(userId));
        return pager;
    }

    /**
     * 获取文章点赞的用户id
     * @param articleId
     * @param pager
     * @return
     */
    @Override
    public Pager<String> getListByArticle(String articleId, Pager<String> pager) {
        List<String> list = likeMapper.getListByArticle(articleId, pager);
        if (list == null) {
            return null;
        }
        pager.setRows(list);
        pager.setTotal(likeMapper.countByArticleId(articleId));
        return pager;
    }

    /**
     * 点赞并给作者发送通知
     * @param userId
     * @param articleId
     * @return
     */
    @Override
    public APIResult like(String userId, String articleId) {
        if (check(userId, articleId)) {
            return APIResult.createNg("不能重复点赞");
        }
        if (likeMapper.like(userId, articleId) == 1) {
            Article article = articleService.getById(articleId);
            if (!userId.equals(article.getUserId())) {
                User user = userService.getById(userId);
                notificationService.insert(new Notification(
                        "like",
                        "用户<a href='/user/" + userId + "'>" +
                                (user.getNickname() != null ? user.getNickname() : user.getUsername()) + "</a>" +
                                "点赞了你的文章<a href='/article/" + articleId + "'>《" + article.getTitle() + "》</a>",
                        article.getUserId()
                ));
            }
            return APIResult.createOk("点赞成功");
        }
        return APIResult.createNg("点赞失败");
    }

    /**
     * 取消点赞
     * @param userId
     * @param articleId
     * @return
     */
    @Override
    public APIResult unlike(String userId, String articleId) {
        if (!check(userId, articleId)) {
            return APIResult.createNg("未点赞");
        }
        if (likeMapper.unlike(userId, articleId) == 1) {
            return APIResult.createOk("取消点赞成功");
        }
        return APIResult.createNg("取消点赞失败");
    }

    /**
     * 检查点赞状态
     * @param userId
     * @param articleId
     * @return
     */
    @Override
    public boolean check(String userId, String articleId) {
        return likeMapper.check(userId, articleId) > 0;
    }

    /**
     * 计算文章点赞数量
     * @param articleId
     * @return
     */
    @Override
    public int countArticle(String articleId) {
        return likeMapper.countByArticleId(articleId);
    }
}
