package ink.wyy.service;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Article;
import ink.wyy.bean.Pager;

public interface ArticleService {

    Pager<Article> getList(String boardId, Pager<Article> pager, String order);

    APIResult insert(Article article);

    APIResult update(Article article);

    APIResult delete(String id);

    Pager<Article> findByTitle(String title, String boardId, Pager<Article> pager, String order);

    Pager<Article> findAll(String title, Pager<Article> pager, String order);

    Article getById(String id);

    void visit(String id);
}
