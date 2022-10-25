package ink.wyy.service.impl;

import ink.wyy.bean.*;
import ink.wyy.mapper.FollowMapper;
import ink.wyy.service.FollowService;
import ink.wyy.service.NotificationService;
import ink.wyy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 板块关注服务
 */
@Service
public class FollowServiceImpl implements FollowService {

    private final FollowMapper followMapper;

    @Autowired
    public FollowServiceImpl(FollowMapper followMapper) {
        this.followMapper = followMapper;
    }

    /**
     * 通过用户id获取关注板块id列表
     * @param userId
     * @param pager
     * @return
     */
    @Override
    public Pager<Board> getListByUser(String userId, Pager<Board> pager) {
        if (pager.getSize() == 0) pager.setSize(20);
        if (pager.getPage() == 0) pager.setPage(1);
        List<Board> list = followMapper.getListByUser(userId, pager);
        if (list == null) {
            return null;
        }
        pager.setRows(list);
        pager.setTotal(followMapper.countByUserId(userId));
        return pager;
    }

    /**
     * 通过板块id获取关注其用户id列表
     * @param boardId
     * @param pager
     * @return
     */
    @Override
    public Pager<User> getListByBoard(String boardId, Pager<User> pager) {
        if (pager.getSize() == 0) pager.setSize(20);
        if (pager.getPage() == 0) pager.setPage(1);
        List<User> list = followMapper.getListByBoard(boardId, pager);
        if (list == null) {
            return null;
        }
        pager.setRows(list);
        pager.setTotal(followMapper.countByBoardId(boardId));
        return pager;
    }

    /**
     * 关注板块
     * @param userId
     * @param articleId
     * @return
     */
    @Override
    public APIResult follow(String userId, String articleId) {
        if (check(userId, articleId)) {
            return APIResult.createNg("不能重复关注");
        }
        if (followMapper.follow(userId, articleId) == 1) {
            return APIResult.createOk("关注成功");
        }
        return APIResult.createNg("关注失败");
    }

    /**
     * 取消关注板块
     * @param userId
     * @param articleId
     * @return
     */
    @Override
    public APIResult unfollow(String userId, String articleId) {
        if (!check(userId, articleId)) {
            return APIResult.createNg("未关注");
        }
        if (followMapper.unfollow(userId, articleId) == 1) {
            return APIResult.createOk("取消关注成功");
        }
        return APIResult.createNg("取消关注失败");
    }

    /**
     * 检查是否关注板块
     * @param userId
     * @param boardId
     * @return
     */
    @Override
    public boolean check(String userId, String boardId) {
        return followMapper.check(userId, boardId) > 0;
    }
}
