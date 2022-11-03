package ink.wyy.service.impl;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Notification;
import ink.wyy.bean.Pager;
import ink.wyy.bean.User;
import ink.wyy.mapper.FriendMapper;
import ink.wyy.service.FriendService;
import ink.wyy.service.NotificationService;
import ink.wyy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {

    private final FriendMapper friendMapper;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public FriendServiceImpl(FriendMapper friendMapper, UserService userService, NotificationService notificationService) {
        this.friendMapper = friendMapper;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     * 获得互关列表
     * @param userId
     * @param pager
     * @return
     */
    @Override
    public Pager<User> getFriends(String userId, Pager<User> pager) {
        if (pager.getSize() == 0) pager.setSize(20);
        if (pager.getPage() == 0) pager.setPage(1);
        List<User> list = friendMapper.getFriends(userId, pager);
        if (list == null) return null;
        pager.setRows(list);
        pager.setTotal(friendMapper.countFriends(userId));
        return pager;
    }

    /**
     * 获得我关注的用户列表
     * @param userId
     * @param pager
     * @return
     */
    @Override
    public Pager<User> getMyFollow(String userId, Pager<User> pager) {
        if (pager.getSize() == 0) pager.setSize(20);
        if (pager.getPage() == 0) pager.setPage(1);
        if (userId == null || userId.equals("")) {
            return null;
        }
        List<User> list = friendMapper.getMyFollow(userId, pager);
        if (list == null) return null;
        pager.setRows(list);
        pager.setTotal(friendMapper.countMyFollow(userId));
        return pager;
    }

    /**
     * 获得关注我的用户列表
     * @param userId
     * @param pager
     * @return
     */
    @Override
    public Pager<User> getFollowMe(String userId, Pager<User> pager) {
        if (pager.getSize() == 0) pager.setSize(20);
        if (pager.getPage() == 0) pager.setPage(1);
        if (userId == null || userId.equals("")) {
            return null;
        }
        List<User> list = friendMapper.getFollowMe(userId, pager);
        if (list == null) return null;
        pager.setRows(list);
        pager.setTotal(friendMapper.countFollowMe(userId));
        return pager;
    }

    /**
     * 关注某用户
     * @param followerId
     * @param userId
     * @return
     */
    @Override
    public APIResult follow(String followerId, String userId) {
        if (userId == null || userId.equals("")) {
            return APIResult.createNg("用户不能为空");
        }
        if (userService.getById(userId) == null) {
            return APIResult.createNg("用户不存在");
        }
        if (followerId.equals(userId)) {
            return APIResult.createNg("不能关注自己");
        }
        if (friendMapper.check(followerId, userId) > 0) {
            return APIResult.createNg("不能重复关注");
        }
        if (friendMapper.follow(followerId, userId) == 1) {
            User user = userService.getById(followerId);
            notificationService.insert(new Notification(
                    "friend",
                    "用户<a href='/user/" + userId + "'>" +
                            (user.getNickname() != null ? user.getNickname() : user.getUsername()) +
                            "</a>关注了你。",
                    userId
            ));
            return APIResult.createOk("关注成功");
        }
        return APIResult.createNg("关注失败");
    }

    /**
     * 取消关注某用户
     * @param followerId
     * @param userId
     * @return
     */
    @Override
    public APIResult unfollow(String followerId, String userId) {
        if (userId == null || userId.equals("")) {
            return APIResult.createNg("用户不能为空");
        }
        if (userService.getById(userId) == null) {
            return APIResult.createNg("用户不存在");
        }
        if (friendMapper.check(followerId, userId) == 0) {
            return APIResult.createNg("您未关注此用户");
        }
        if (friendMapper.unfollow(followerId, userId) == 1) {
            return APIResult.createOk("取消关注成功");
        }
        return APIResult.createNg("取消关注失败");
    }

    /**
     * 检查是否关注
     * @param followerId
     * @param userId
     * @return
     */
    @Override
    public APIResult check(String followerId, String userId) {
        if (userId == null || userId.equals("")) {
            return APIResult.createNg("用户不能为空");
        }
        if (friendMapper.check(followerId, userId) > 0) {
            return APIResult.createOk("关注成功");
        }
        return APIResult.createNg("未关注");
    }

    /**
     * 统计我的关注（加互关）
     * @param userId
     * @return
     */
    @Override
    public APIResult countMyFollow(String userId) {
        if (userId == null || userId.equals("")) {
            return APIResult.createNg("用户不能为空");
        }
        int cnt = friendMapper.countMyFollow(userId) + friendMapper.countFriends(userId);
        return APIResult.createOk(cnt);
    }

    /**
     * 统计关注我的（加互关）
     * @param userId
     * @return
     */
    @Override
    public APIResult countFollowMe(String userId) {
        if (userId == null || userId.equals("")) {
            return APIResult.createNg("用户不能为空");
        }
        int cnt = friendMapper.countFollowMe(userId) + friendMapper.countFriends(userId);
        return APIResult.createOk(cnt);
    }
}
