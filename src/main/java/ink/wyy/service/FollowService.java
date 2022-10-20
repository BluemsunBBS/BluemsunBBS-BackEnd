package ink.wyy.service;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Pager;

public interface FollowService {

    Pager<String> getListByUser(String userId, Pager<String> pager);

    Pager<String> getListByBoard(String boardId, Pager<String> pager);

    APIResult follow(String userId, String boardId);

    APIResult unfollow(String userId, String boardId);

    boolean check(String userId, String boardId);
}
