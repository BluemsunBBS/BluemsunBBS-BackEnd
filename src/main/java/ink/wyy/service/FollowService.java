package ink.wyy.service;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Board;
import ink.wyy.bean.Pager;
import ink.wyy.bean.User;

public interface FollowService {

    Pager<Board> getListByUser(String userId, Pager<Board> pager);

    Pager<User> getListByBoard(String boardId, Pager<User> pager);

    APIResult follow(String userId, String boardId);

    APIResult unfollow(String userId, String boardId);

    boolean check(String userId, String boardId);
}
