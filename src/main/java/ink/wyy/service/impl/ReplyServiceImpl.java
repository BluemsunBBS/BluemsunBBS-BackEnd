package ink.wyy.service.impl;

import ink.wyy.bean.*;
import ink.wyy.mapper.ReplyMapper;
import ink.wyy.service.ArticleService;
import ink.wyy.service.NotificationService;
import ink.wyy.service.ReplyService;
import ink.wyy.service.UserService;
import ink.wyy.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplyServiceImpl implements ReplyService {

    private final ReplyMapper replyMapper;
    private final ArticleService articleService;
    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public ReplyServiceImpl(ReplyMapper replyMapper,
                            ArticleService articleService,
                            NotificationService notificationService,
                            UserService userService) {
        this.replyMapper = replyMapper;
        this.articleService = articleService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @Override
    public Pager<Reply> getReply(String articleId, Pager<Reply> pager) {
        if (pager.getPage() == 0) pager.setPage(1);
        if (pager.getSize() == 0) pager.setSize(20);
        Article article = articleService.getById(articleId);
        if (article == null) {
            return null;
        }
        try {
            List<Reply> list = replyMapper.selectByArticle(articleId, pager);
            if (list == null) {
                return null;
            }
            Pager<Reply> sonPager = new Pager<>();
            sonPager.setSize(99);
            sonPager.setPage(1);
            for (Reply r : list) {
                r.setUser(userService.getById(r.getUserId()));
                List<Reply> sonList = replyMapper.selectByReply(r.getId(), sonPager);
                if (sonList != null) {
                    for (Reply r2 : sonList) {
                        r2.setUser(userService.getById(r2.getUserId()));
                    }
                    r.setReplies(sonList);
                }
            }
            pager.setRows(list);
            pager.setTotal(replyMapper.count(articleId));
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Pager<Reply> getReplyByReply(String replyId, Pager<Reply> pager) {
        if (pager.getPage() == 0) pager.setPage(1);
        if (pager.getSize() == 0) pager.setSize(5);
        Reply reply = replyMapper.getById(replyId);
        if (reply == null) {
            return null;
        }
        try {
            List<Reply> list = replyMapper.selectByReply(replyId, pager);
            if (list == null) {
                return null;
            }
            for (Reply r : list) {
                r.setUser(userService.getById(r.getUserId()));
            }
            pager.setRows(list);
            pager.setTotal(replyMapper.count(replyId));
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public APIResult insert(Reply reply) {
        if (reply.getArticleId() == null || reply.getArticleId().equals("")) {
            if (reply.getReplyId() == null || reply.getReplyId().equals("")) {
                return APIResult.createNg("??????id????????????");
            }
        }
        if (reply.getUserId() == null || reply.getUserId().equals("")) {
            return APIResult.createNg("??????????????????");
        }
        Article article = null;
        Reply reply1;
        String repliedUserId = null;
        if (reply.getArticleId() != null) {
            article = articleService.getById(reply.getArticleId());
            if (article == null) {
                return APIResult.createNg("???????????????");
            }
            repliedUserId = article.getUserId();
        }
        if (reply.getReplyId() != null) {
            reply1 = replyMapper.getById(reply.getReplyId());
            if (reply1 == null) {
                return APIResult.createNg("???????????????");
            }
            repliedUserId = reply1.getUserId();
            article = articleService.getById(reply1.getArticleId());
        }
        if (article == null) {
            return APIResult.createNg("???????????????");
        }
        User user = userService.getById(reply.getUserId());
        reply.setId(UUIDUtil.get());
        try {
            if (replyMapper.insert(reply) == 1) {
                String noticeText = "???????????????????????? @" + user.getUsername() + " ????????????";
                if (reply.getArticleId() != null) {
                    noticeText += "???????????????" + article.getTitle() + "??????";
                } else {
                    noticeText += "???????????????" + article.getTitle() + "??????????????????";
                }
                noticeText += "???????????????<a href=\"/article/" + article.getId() + "\">" +
                        article.getTitle() + "</a>";
                notificationService.insert(new Notification(
                        "reply",
                        noticeText,
                        repliedUserId
                ));
                return APIResult.createOk("????????????");
            } else {
                return APIResult.createNg("????????????");
            }
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    @Override
    public APIResult delete(String id) {
        if (id == null || id.equals("")) {
            return APIResult.createNg("??????id????????????");
        }
        try {
            if (replyMapper.delete(id) == 1) {
                return APIResult.createOk("????????????");
            } else {
                return APIResult.createNg("???????????????");
            }
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    @Override
    public Reply getById(String id) {
        if (id == null || id.equals("")) {
            return null;
        }
        try {
            Reply reply = replyMapper.getById(id);
            if (reply == null) return null;
            reply.setUser(userService.getById(reply.getUserId()));
            return reply;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int countByArticle(String articleId) {
        return replyMapper.count(articleId);
    }
}
