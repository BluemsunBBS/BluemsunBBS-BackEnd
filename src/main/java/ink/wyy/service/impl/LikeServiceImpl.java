package ink.wyy.service.impl;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Pager;
import ink.wyy.mapper.LikeMapper;
import ink.wyy.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;

    @Autowired
    public LikeServiceImpl(LikeMapper likeMapper) {
        this.likeMapper = likeMapper;
    }

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

    @Override
    public APIResult like(String userId, String articleId) {
        if (check(userId, articleId)) {
            return APIResult.createNg("不能重复点赞");
        }
        if (likeMapper.like(userId, articleId) == 1) {
            return APIResult.createOk("点赞成功");
        }
        return APIResult.createNg("点赞失败");
    }

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

    @Override
    public boolean check(String userId, String articleId) {
        return likeMapper.check(userId, articleId) > 0;
    }
}
