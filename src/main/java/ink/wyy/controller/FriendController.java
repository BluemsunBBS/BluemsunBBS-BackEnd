package ink.wyy.controller;

import ink.wyy.auth.LoginAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.bean.Pager;
import ink.wyy.bean.User;
import ink.wyy.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@LoginAuth
public class FriendController {

    private final FriendService friendService;

    @Autowired
    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping("/friend/friendList/{userId}")
    public APIResult getFriend(HttpServletRequest request,
                               Pager<User> pager,
                               @PathVariable String userId) {
        pager = friendService.getFriends(userId, pager);
        if (pager == null) {
            return APIResult.createNg("获取失败");
        }
        return APIResult.createOk(pager);
    }

    @GetMapping("/friend/followList/{userId}")
    public APIResult getMyFollow(HttpServletRequest request,
                                 Pager<User> pager,
                                 @PathVariable String userId) {
        pager = friendService.getMyFollow(userId, pager);
        if (pager == null) {
            return APIResult.createNg("获取失败");
        }
        return APIResult.createOk(pager);
    }

    @GetMapping("/friend/fansList/{userId}")
    public APIResult getFollowMe(HttpServletRequest request,
                                 Pager<User> pager,
                                 @PathVariable String userId) {
        pager = friendService.getFollowMe(userId, pager);
        if (pager == null) {
            return APIResult.createNg("获取失败");
        }
        return APIResult.createOk(pager);
    }

    @PostMapping("/friend/{id}")
    public APIResult follow(HttpServletRequest request, @PathVariable String id) {
        User user = (User) request.getAttribute("user");
        return friendService.follow(user.getId(), id);
    }

    @DeleteMapping("/friend/{id}")
    public APIResult unfollow(HttpServletRequest request, @PathVariable String id) {
        User user = (User) request.getAttribute("user");
        return friendService.unfollow(user.getId(), id);
    }

    @GetMapping("/friend/check/{id}")
    public APIResult check(HttpServletRequest request, @PathVariable String id) {
        User user = (User) request.getAttribute("user");
        return friendService.check(user.getId(), id);
    }

    @GetMapping("/friend/countFollow/{id}")
    @LoginAuth(value = false)
    public APIResult countFollow(@PathVariable String id) {
        return friendService.countMyFollow(id);
    }

    @GetMapping("/friend/countFans/{id}")
    @LoginAuth(value = false)
    public APIResult countFans(@PathVariable String id) {
        return friendService.countFollowMe(id);
    }
}
