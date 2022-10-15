package ink.wyy.service.impl;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Article;
import ink.wyy.bean.Board;
import ink.wyy.bean.Pager;
import ink.wyy.mapper.ArticleMapper;
import ink.wyy.mapper.BoardMapper;
import ink.wyy.service.ArticleService;
import ink.wyy.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final BoardMapper boardMapper;
    private final ArticleMapper articleMapper;

    @Autowired
    public ArticleServiceImpl(BoardMapper boardMapper, ArticleMapper articleMapper) {
        this.boardMapper = boardMapper;
        this.articleMapper = articleMapper;
    }

    @Override
    public Pager<Article> getList(String boardId, Pager<Article> pager, String order) {
        if (pager.getSize() == 0) pager.setSize(20);
        if (pager.getPage() == 0) pager.setPage(1);
        Board board;
        try {
            board = boardMapper.getById(boardId);
        } catch (Exception e) {
            return null;
        }
        if (board == null) {
            return null;
        }
        try {
            if (order == null || order.equals("")) {
                order = "update_time desc";
            }
            List<Article> list = articleMapper.selectByBoard(boardId, pager, order);
            if (list == null) return null;
            pager.setRows(list);
            pager.setTotal(articleMapper.count(boardId));
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public APIResult insert(Article article) {
        if (article.getTitle() == null || article.getTitle().equals("")) {
            return APIResult.createNg("题目不能为空");
        }
        if (article.getBoardId() == null || article.getBoardId().equals("")) {
            return APIResult.createNg("所属板块不能为空");
        }
        if (article.getUserId() == null || article.getUserId().equals("")) {
            return APIResult.createNg("作者不能为空");
        }
        article.setId(UUIDUtil.get());
        try {
            if (articleMapper.insert(article) == 1) {
                article = articleMapper.getById(article.getId());
                Board board = boardMapper.getById(article.getBoardId());
                board.setUpdateTime(new Date());
                boardMapper.update(board);
                return APIResult.createOk(article);
            } else {
                return APIResult.createNg("发布失败");
            }
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    @Override
    public APIResult update(Article article) {
        Article old;
        try {
            old = articleMapper.getById(article.getId());
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
        if (old == null) {
            return APIResult.createNg("帖子不存在");
        }
        if (article.getTitle() != null && !article.getTitle().equals("")) {
            old.setTitle(article.getTitle());
        }
        if (article.getTop() != null) {
            old.setTop(article.getTop());
        }
        if (article.getText() != null && !article.getText().equals("")) {
            old.setText(article.getText());
        }
        if (article.getFiles() != null && !article.getFiles().equals("")) {
            old.setFiles(article.getFiles());
        }
        if (article.getBoardId() != null && !article.getBoardId().equals("")) {
            Board board;
            try {
                board = boardMapper.getById(article.getBoardId());
            } catch (Exception e) {
                return APIResult.createNg("板块不存在");
            }
            if (board == null) {
                return APIResult.createNg("板块不存在");
            }
            old.setBoardId(article.getBoardId());
        }
        try {
            if (articleMapper.update(old) == 1) {
                Board board = boardMapper.getById(old.getBoardId());
                board.setUpdateTime(new Date());
                boardMapper.update(board);
                return APIResult.createOk("更新成功");
            } else {
                return APIResult.createNg("更新失败");
            }
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    @Override
    public APIResult delete(String id) {
        try {
            Article article = articleMapper.getById(id);
            if (article == null) {
                return APIResult.createNg("帖子不存在");
            }
            articleMapper.delete(id);
            return APIResult.createOk("删除成功");
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    @Override
    public Pager<Article> findByTitle(String title, String boardId, Pager<Article> pager, String order) {
        if (title == null || title.equals("")) {
            return getList(boardId, pager, order);
        }
        if (pager.getSize() == 0) pager.setSize(20);
        if (pager.getPage() == 0) pager.setPage(1);
        if (order == null || order.equals("")) {
            order = "update_time desc";
        }
        Board board;
        try {
            board = boardMapper.getById(boardId);
        } catch (Exception e) {
            return null;
        }
        if (board == null) {
            return null;
        }
        title = '%' + title + '%';
        try {
            List<Article> list = articleMapper.findByTitle(boardId, title, pager, order);
            if (list == null) {
                return null;
            }
            pager.setTotal(articleMapper.countByTitle(boardId, title));
            pager.setRows(list);
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Pager<Article> findAll(String title, Pager<Article> pager, String order) {
        if (pager.getSize() == 0) pager.setSize(20);
        if (pager.getPage() == 0) pager.setPage(1);
        if (order == null || order.equals("")) {
            order = "update_time desc";
        }
        title = '%' + title + '%';
        try {
            List<Article> list = articleMapper.findAll(title, pager, order);
            if (list == null) {
                return null;
            }
            pager.setTotal(articleMapper.countByTitleAll(title));
            pager.setRows(list);
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Article getById(String id) {
        if (id == null || id.equals("")) {
            return null;
        }
        try {
            return articleMapper.getById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void visit(String id) {
        if (id == null || id.equals("")) {
            return;
        }
        try {
            articleMapper.visit(id);
        } catch (Exception ignored) {
        }
    }
}
