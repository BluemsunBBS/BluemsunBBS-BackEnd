package ink.wyy.service;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Pager;
import ink.wyy.bean.Reply;

public interface ReplyService {

    Pager<Reply> getReply(String articleId, Pager<Reply> pager);

    Pager<Reply> getReplyByReply(String replyId, Pager<Reply> pager);

    APIResult insert(Reply reply);

    APIResult delete(String id);

    Reply getById(String id);

    int countByArticle(String articleId);
}
