package ink.wyy.service.impl;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Pager;
import ink.wyy.mapper.FollowMapper;
import ink.wyy.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowMapper followMapper;

    @Autowired
    public FollowServiceImpl(FollowMapper followMapper) {
        this.followMapper = followMapper;
    }

    @Override
    public Pager<String> getListByUser(String userId, Pager<String> pager) {
        List<String> list = followMapper.getListByUser(userId, pager);
        if (list == null) {
            return null;
        }
        pager.setRows(list);
        pager.setTotal(followMapper.countByUserId(userId));
        return pager;
    }

    @Override
    public Pager<String> getListByBoard(String boardId, Pager<String> pager) {
        List<String> list = followMapper.getListByBoard(boardId, pager);
        if (list == null) {
            return null;
        }
        pager.setRows(list);
        pager.setTotal(followMapper.countByBoardId(boardId));
        return pager;
    }

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

    @Override
    public boolean check(String userId, String boardId) {
        return followMapper.check(userId, boardId) > 0;
    }
}
