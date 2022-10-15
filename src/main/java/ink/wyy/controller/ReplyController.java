package ink.wyy.controller;

import ink.wyy.auth.LoginAuth;
import ink.wyy.auth.ReplyerAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.bean.Pager;
import ink.wyy.bean.Reply;
import ink.wyy.bean.User;
import ink.wyy.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@LoginAuth
@RequestMapping("/reply")
public class ReplyController {

    private final ReplyService replyService;

    @Autowired
    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @PostMapping("/")
    public APIResult createReply(@RequestBody Reply reply,
                                 HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        reply.setUserId(user.getId());
        return replyService.insert(reply);
    }

    @DeleteMapping("/{replyId}")
    @ReplyerAuth
    public APIResult deleteReply(@PathVariable("replyId") String id) {
        return replyService.delete(id);
    }

    @GetMapping("/list/{articleId}")
    @LoginAuth(value = false)
    public APIResult getList(@PathVariable("articleId") String articleId,
                             Pager<Reply> pager) {
        pager = replyService.getReply(articleId, pager);
        if (pager == null) {
            return APIResult.createNg("获取失败");
        }
        return APIResult.createOk(pager);
    }

    @GetMapping("/list-reply/{replyId}")
    @LoginAuth(value = false)
    public APIResult getListByReply(@PathVariable("replyId") String replyId,
                                    Pager<Reply> pager) {
        pager = replyService.getReplyByReply(replyId, pager);
        if (pager == null) {
            return APIResult.createNg("获取失败");
        }
        return APIResult.createOk(pager);
    }
}
