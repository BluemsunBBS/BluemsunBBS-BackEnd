package ink.wyy.controller;

import ink.wyy.auth.LoginAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.bean.Board;
import ink.wyy.bean.Pager;
import ink.wyy.bean.User;
import ink.wyy.service.FollowService;
import ink.wyy.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@LoginAuth
public class FollowController {

    private final FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/follow/{boardId}")
    public APIResult like(@PathVariable String boardId,
                          HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return followService.follow(user.getId(), boardId);
    }

    @DeleteMapping("/follow/{boardId}")
    public APIResult unlike(@PathVariable String boardId,
                            HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return followService.unfollow(user.getId(), boardId);
    }

    @GetMapping("/follow/list/{boardId}")
    @LoginAuth(value = false)
    public APIResult getListByArticle(@PathVariable String boardId,
                                      Pager<User> pager) {
        pager = followService.getListByBoard(boardId, pager);
        if (pager == null) {
            return APIResult.createNg("获取失败");
        }
        return APIResult.createOk(pager);
    }

    @GetMapping("/follow/listBoard/{userId}")
    public APIResult getListByUser(@PathVariable String userId,
                                   HttpServletRequest request,
                                   Pager<Board> pager) {
        pager = followService.getListByUser(userId, pager);
        if (pager == null) {
            return APIResult.createNg("获取失败");
        }
        return APIResult.createOk(pager);
    }

    @GetMapping("/follow/{boardId}")
    public APIResult check(HttpServletRequest request, @PathVariable String boardId) {
        User user = (User) request.getAttribute("user");
        if (followService.check(user.getId(), boardId)) {
            return APIResult.createOk("关注检查成功");
        }
        return APIResult.createNg("关注不存在");
    }
}

