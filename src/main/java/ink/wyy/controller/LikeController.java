package ink.wyy.controller;

import ink.wyy.auth.LoginAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.bean.Pager;
import ink.wyy.bean.User;
import ink.wyy.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@LoginAuth
public class LikeController {

    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/like/{articleId}")
    public APIResult like(@PathVariable String articleId,
                          HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return likeService.like(user.getId(), articleId);
    }

    @DeleteMapping("/like/{articleId}")
    public APIResult unlike(@PathVariable String articleId,
                            HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return likeService.unlike(user.getId(), articleId);
    }

    @GetMapping("/like/list/{articleId}")
    @LoginAuth(value = false)
    public APIResult getListByArticle(@PathVariable String articleId,
                                      Pager<String> pager) {
        pager = likeService.getListByArticle(articleId, pager);
        if (pager == null) {
            return APIResult.createNg("获取失败");
        }
        return APIResult.createOk(pager);
    }

    @GetMapping("/like/list")
    public APIResult getListByUser(HttpServletRequest request,
                                   Pager<String> pager) {
        User user = (User) request.getAttribute("user");
        pager = likeService.getListByUser(user.getId(), pager);
        if (pager == null) {
            return APIResult.createNg("获取失败");
        }
        return APIResult.createOk(pager);
    }

    @GetMapping("/like/{articleId}")
    public APIResult check(HttpServletRequest request, @PathVariable String articleId) {
        User user = (User) request.getAttribute("user");
        if (likeService.check(user.getId(), articleId)) {
            return APIResult.createOk("点赞检查成功");
        }
        return APIResult.createNg("点赞不存在");
    }
}
