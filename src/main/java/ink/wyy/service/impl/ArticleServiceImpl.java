package ink.wyy.service.impl;

import ink.wyy.bean.*;
import ink.wyy.mapper.ArticleMapper;
import ink.wyy.mapper.LikeMapper;
import ink.wyy.mapper.ReplyMapper;
import ink.wyy.mapper.UserMapper;
import ink.wyy.service.*;
import ink.wyy.util.RedisUtil;
import ink.wyy.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final BoardService boardService;
    private final ArticleMapper articleMapper;
    private final NotificationService notificationService;
    private final LikeMapper likeMapper;
    private final ReplyMapper replyMapper;
    private final UserMapper userMapper;
    private final RedisUtil redisUtil;

    @Autowired
    public ArticleServiceImpl(BoardService boardService,
                              ArticleMapper articleMapper,
                              NotificationService notificationService, LikeMapper likeMapper, ReplyMapper replyMapper, UserMapper userMapper, RedisUtil redisUtil) {
        this.boardService = boardService;
        this.articleMapper = articleMapper;
        this.notificationService = notificationService;
        this.likeMapper = likeMapper;
        this.replyMapper = replyMapper;
        this.userMapper = userMapper;
        this.redisUtil = redisUtil;
    }

    /**
     * 获取文章列表
     * @param boardId
     * @param pager
     * @param order
     * @param userId
     * @return
     */
    @Override
    public Pager<Article> getList(String boardId, Pager<Article> pager, String order, String userId) {
//        if (pager.getSize() == 0) pager.setSize(20);
//        if (pager.getPage() == 0) pager.setPage(1);
//        Board board;
//        try {
//            board = boardService.getById(boardId);
//        } catch (Exception e) {
//            return null;
//        }
//        if (board == null) {
//            return null;
//        }
        try {
            if (order == null || order.equals("")) {
                order = "update_time desc";
            }
            List<Article> list = articleMapper.selectByBoard(boardId, pager, order);
            if (list == null) return null;
            if (userId != null) {
                for (Article article : list) {
                    article.setCountLike(likeMapper.countByArticleId(article.getId()));
                    article.setCountReply(replyMapper.count(article.getId()));
                    article.setNickname(userMapper.findById(article.getUserId()).getNickname());
                    article.setIsLike(likeMapper.check(userId, article.getId()) == 1);
                    article.setBoardName(boardService.getById(article.getBoardId()).getName());
                }
            }
            pager.setRows(list);
            pager.setTotal(articleMapper.count(boardId));
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 发布文章
     * @param article
     * @return
     */
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
        // 防止用户绕过审核
        if (article.getApproved() == 1) {
            article.setApproved(0);
        }
        try {
            if (articleMapper.insert(article) == 1) {
                // 如果发布的是草稿，不进行通知
                if (article.getApproved() == 3) {
                    return APIResult.createOk(article);
                }
                article = articleMapper.getById(article.getId());
                noticeHost(article);
                return APIResult.createOk(article);
            } else {
                return APIResult.createNg("发布失败");
            }
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    /**
     * 更新文章
     * @param article
     * @return
     */
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
                board = boardService.getById(article.getBoardId());
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
                Board board = boardService.getById(old.getBoardId());
                board.setUpdateTime(new Date());
                boardService.update(board);
                return APIResult.createOk("更新成功");
            } else {
                return APIResult.createNg("更新失败");
            }
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    /**
     * 删除文章
     * @param id
     * @return
     */
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

    /**
     * 通过题目搜索某板块文章
     * @param title
     * @param boardId
     * @param pager
     * @param order
     * @param userId
     * @return
     */
    @Override
    public Pager<Article> findByTitle(String title, String boardId, Pager<Article> pager, String order, String userId) {
        if (title == null || title.equals("")) {
            return getList(boardId, pager, order, userId);
        }
        if (pager.getSize() == 0) pager.setSize(20);
        if (pager.getPage() == 0) pager.setPage(1);
        if (order == null || order.equals("")) {
            order = "update_time desc";
        }
        Board board;
        try {
            board = boardService.getById(boardId);
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
            for (Article article : list) {
                article.setCountLike(likeMapper.countByArticleId(article.getId()));
                article.setCountReply(replyMapper.count(article.getId()));
                article.setNickname(userMapper.findById(article.getUserId()).getNickname());
                article.setIsLike(likeMapper.check(userId, article.getId()) == 1);
                article.setBoardName(board.getName());
            }
            pager.setTotal(articleMapper.countByTitle(boardId, title));
            pager.setRows(list);
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过题目搜索所有文章
     * @param title
     * @param pager
     * @param order
     * @param userId
     * @return
     */
    @Override
    public Pager<Article> findAll(String title, Pager<Article> pager, String order, String userId) {
//        if (pager.getSize() == 0) pager.setSize(20);
//        if (pager.getPage() == 0) pager.setPage(1);
        if (order == null || order.equals("")) {
            order = "update_time desc";
        }
        title = '%' + title + '%';
        try {
            List<Article> list = articleMapper.findAll(title, pager, order);
            if (list == null) {
                return null;
            }
            for (Article article : list) {
                article.setCountLike(likeMapper.countByArticleId(article.getId()));
                article.setCountReply(replyMapper.count(article.getId()));
                article.setNickname(userMapper.findById(article.getUserId()).getNickname());
                article.setIsLike(likeMapper.check(userId, article.getId()) == 1);
                article.setBoardName(boardService.getById(article.getBoardId()).getName());
            }
            pager.setTotal(articleMapper.countByTitleAll(title));
            pager.setRows(list);
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过用户id或板块id获取该用户的草稿箱或该板块的待审核文章
     * @param id
     * @param state
     * @param pager
     * @return
     */
    @Override
    public Pager<Article> approvedList(String id, int state, Pager<Article> pager) {
        if (id == null || id.equals("")) {
            return null;
        }
        try {
            List<Article> list = articleMapper.approvedList(id, state, pager);
            if (list == null) {
                return null;
            }
            for (Article article : list) {
                article.setNickname(userMapper.findById(article.getUserId()).getNickname());
                article.setBoardName(boardService.getById(article.getBoardId()).getName());
            }
            pager.setTotal(articleMapper.countApproved(id, state));
            pager.setRows(list);
            return pager;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * 审核文章或从草稿箱中发布文章
     * @param id
     * @param state
     * @return
     */
    @Override
    public APIResult approve(String id, int state) {
        if (id == null || id.equals("")) {
            return APIResult.createNg("id不能为空");
        }
        try {
            if (articleMapper.setApprove(id, state) == 1) {
                Article article = articleMapper.getById(id);
                if (state == 1) {
                    User user = userMapper.findById(article.getUserId());
                    notificationService.insert(new Notification(
                            "system",
                            "【审核通过通知】您的文章《" + article.getTitle() + "》审核通过。",
                            user.getId()
                    ));
                    return APIResult.createOk("审核成功");
                } else {
                    noticeHost(article);
                    return APIResult.createOk("发布成功");
                }
            }
            return APIResult.createNg("失败");
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    private void noticeHost(Article article) {
        Board board = boardService.getById(article.getBoardId());
        board.setUpdateTime(new Date());
        boardService.update(board);
        List<User> hostList = (List<User>) boardService.getHostList(board.getId()).getData();
        for (User user : hostList) {
            notificationService.insert(new Notification(
                    "system",
                    "【文章审核通知】板块\"" + board.getName() + "\" 新文章《" + article.getTitle() + "》发布了，请即时审核。",
                    user.getId()
            ));
        }
    }

    /**
     * 通过id获取文章和附加信息
     * @param id
     * @param userId
     * @return
     */
    @Override
    public Article getById(String id, String userId) {
        if (id == null || id.equals("")) {
            return null;
        }
        try {
            Article article = articleMapper.getById(id);
            if (article.getApproved() != 1 && !article.getUserId().equals(userId)) {
                return null;
            }
            article.setCountLike(likeMapper.countByArticleId(article.getId()));
            article.setCountReply(replyMapper.count(article.getId()));
            article.setNickname(userMapper.findById(article.getUserId()).getNickname());
            article.setIsLike(likeMapper.check(userId, article.getId()) == 1);
            article.setBoardName(boardService.getById(article.getBoardId()).getName());
            return article;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过id获取文章
     * @param id
     * @return
     */
    @Override
    public Article getById(String id) {
        if (id == null || id.equals("")) {
            return null;
        }
        try {
            Article article = articleMapper.getById(id);
            if (article.getApproved() != 1) return null;
            return article;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 增加文章访问量
     * TODO
     * @param id
     */
    @Override
    public void visit(String id) {
        if (id == null || id.equals("")) {
            return;
        }
        try {
            int num = Integer.parseInt(redisUtil.get("articleVisit", id));
            redisUtil.set("articleVisit", id, String.valueOf(num + 1));
            int oldNum = Integer.parseInt(redisUtil.get("articleVisitOld", id));
            if (1.0 * Math.abs(oldNum - num) / num > 0.05) {
                redisUtil.set("articleVisitOld", id, String.valueOf(num + 1));
                articleMapper.visit(id, num + 1);
            }
        } catch (Exception ignored) {
        }
    }
}
