package ink.wyy.service;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Pager;

public interface LikeService {

    Pager<String> getListByUser(String userId, Pager<String> pager);

    Pager<String> getListByArticle(String articleId, Pager<String> pager);

    APIResult like(String userId, String articleId);

    APIResult unlike(String userId, String articleId);

    boolean check(String userId, String articleId);
}
