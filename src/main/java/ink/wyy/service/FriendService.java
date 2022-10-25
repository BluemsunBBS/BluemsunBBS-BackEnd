package ink.wyy.service;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Pager;
import ink.wyy.bean.User;

public interface FriendService {

    Pager<User> getFriends(String userId, Pager<User> pager);

    Pager<User> getMyFollow(String userId, Pager<User> pager);

    Pager<User> getFollowMe(String userId, Pager<User> pager);

    APIResult follow(String followerId, String userId);

    APIResult unfollow(String followerId, String userId);

    APIResult check(String followerId, String userId);

    APIResult countMyFollow(String userId);

    APIResult countFollowMe(String userId);
}
