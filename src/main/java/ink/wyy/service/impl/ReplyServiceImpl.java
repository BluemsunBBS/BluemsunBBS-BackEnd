package ink.wyy.service.impl;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Article;
import ink.wyy.bean.Pager;
import ink.wyy.bean.Reply;
import ink.wyy.mapper.ReplyMapper;
import ink.wyy.service.ArticleService;
import ink.wyy.service.ReplyService;
import ink.wyy.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplyServiceImpl implements ReplyService {

    private final ReplyMapper replyMapper;
    private final ArticleService articleService;

    @Autowired
    public ReplyServiceImpl(ReplyMapper replyMapper, ArticleService articleService) {
        this.replyMapper = replyMapper;
        this.articleService = articleService;
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
            sonPager.setSize(2);
            sonPager.setPage(1);
            for (Reply r : list) {
                List<Reply> sonList = replyMapper.selectByReply(r.getId(), sonPager);
                if (sonList != null) {
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
                return APIResult.createNg("回复id不能为空");
            }
        }
        if (reply.getUserId() == null || reply.getUserId().equals("")) {
            return APIResult.createNg("用户不能为空");
        }
        reply.setId(UUIDUtil.get());
        try {
            if (replyMapper.insert(reply) == 1) {
                return APIResult.createOk("回复成功");
            } else {
                return APIResult.createNg("回复失败");
            }
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    @Override
    public APIResult delete(String id) {
        if (id == null || id.equals("")) {
            return APIResult.createNg("评论id不能为空");
        }
        try {
            if (replyMapper.delete(id) == 1) {
                return APIResult.createOk("删除成功");
            } else {
                return APIResult.createNg("评论不存在");
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
            return replyMapper.getById(id);
        } catch (Exception e) {
            return null;
        }
    }
}
