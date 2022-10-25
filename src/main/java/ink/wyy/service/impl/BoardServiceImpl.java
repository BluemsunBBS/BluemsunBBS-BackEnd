package ink.wyy.service.impl;

import ink.wyy.bean.*;
import ink.wyy.mapper.BoardMapper;
import ink.wyy.mapper.UserMapper;
import ink.wyy.service.BoardService;
import ink.wyy.service.NotificationService;
import ink.wyy.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    @Autowired
    public BoardServiceImpl(BoardMapper boardMapper, UserMapper userMapper, NotificationService notificationService) {
        this.boardMapper = boardMapper;
        this.userMapper = userMapper;
        this.notificationService = notificationService;
    }

    /**
     * 添加板块
     * @param board
     * @return
     */
    @Override
    public APIResult insert(Board board) {
        if (board.getName() == null || board.getName().equals("")) {
            return APIResult.createNg("板块主题不能为空");
        }
        board.setId(UUIDUtil.get());
        try {
            if (boardMapper.insert(board) == 1) {
                board = boardMapper.getById(board.getId());
                return APIResult.createOk(board);
            }
            return APIResult.createNg("创建板块失败");
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    /**
     * 更新板块信息
     * @param board
     * @return
     */
    @Override
    public APIResult update(Board board) {
        Board old;
        try {
            old = boardMapper.getById(board.getId());
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
        if (old == null) {
            return APIResult.createNg("板块不存在");
        }
        if (board.getName() != null && !board.getName().equals("")) {
            old.setName(board.getName());
        }
        if (board.getImg() != null && !board.getImg().equals("")) {
            old.setImg(board.getImg());
        }
        if (board.getDescription() != null && !board.getDescription().equals("")) {
            old.setDescription(board.getDescription());
        }
        try {
            if (boardMapper.update(old) == 1) {
                return APIResult.createOk("更新成功");
            }
            return APIResult.createNg("更新失败");
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    /**
     * 删除板块
     * @param id
     * @return
     */
    @Override
    public APIResult delete(String id) {
        try {
            Board board = boardMapper.getById(id);
            if (board == null) {
                return APIResult.createNg("板块不存在");
            }
            boardMapper.delete(id);
            return APIResult.createOk("删除成功");
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    /**
     * 获取板块列表
     * @param pager
     * @return
     */
    @Override
    public Pager<Board> getList(Pager<Board> pager) {
        if (pager.getSize() == 0) pager.setSize(20);
        if (pager.getPage() == 0) pager.setPage(1);
        try {
            List<Board> list = boardMapper.selectAll(pager);
            pager.setRows(list);
            pager.setTotal(boardMapper.count());
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查找板块
     * @param name
     * @param pager
     * @return
     */
    @Override
    public Pager<Board> find(String name, Pager<Board> pager) {
        if (pager.getSize() == 0) pager.setSize(20);
        if (pager.getPage() == 0) pager.setPage(1);
        name = '%' + name + '%';
        try {
            List<Board> list = boardMapper.find(name, pager);
            pager.setRows(list);
            pager.setTotal(boardMapper.countByName(name));
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过id获取板块
     * @param id
     * @return
     */
    @Override
    public Board getById(String id) {
        try {
            return boardMapper.getById(id);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 添加主持人
     * @param userId
     * @param boardId
     * @return
     */
    @Override
    public APIResult addHost(String userId, String boardId) {
        if (userId == null || userId.equals("")) {
            return APIResult.createNg("用户id不能为空");
        }
        if (boardId == null || boardId.equals("")) {
            return APIResult.createNg("板块id不能为空");
        }
        if (checkHost(userId, boardId)) {
            return APIResult.createNg("用户已是该板块主持人");
        }
        if (boardMapper.addHost(userId, boardId) == 1) {
            Board board = getById(boardId);
            notificationService.insert(new Notification(
                    "system",
                    "【用户权限变更通知】您已被设置为板块\"" + board.getName() + "\"的主持人。",
                    userId
            ));
            return APIResult.createOk("添加主持人成功");
        }
        deleteHost(userId, boardId);
        return APIResult.createNg("添加主持人失败");
    }

    /**
     * 检查主持人身份
     * @param userId
     * @param boardId
     * @return
     */
    @Override
    public boolean checkHost(String userId, String boardId) {
        return boardMapper.checkHost(userId, boardId) > 0;
    }

    /**
     * 删除主持人
     * @param userId
     * @param boardId
     * @return
     */
    @Override
    public APIResult deleteHost(String userId, String boardId) {
        if (userId == null || userId.equals("")) {
            return APIResult.createNg("用户id不能为空");
        }
        if (boardId == null || boardId.equals("")) {
            return APIResult.createNg("板块id不能为空");
        }
        if (boardMapper.deleteHost(userId, boardId) == 1) {
            Board board = getById(boardId);
            notificationService.insert(new Notification(
                    "system",
                    "【用户权限变更通知】您已被取消板块\"" + board.getName() + "\"的主持人。",
                    userId
            ));
            return APIResult.createOk("撤销主持人成功");
        }
        return APIResult.createNg("该用户不是此板块主持人");
    }

    /**
     * 通过板块id获得某板块主持人列表
     * @param boardId
     * @return
     */
    @Override
    public APIResult getHostList(String boardId) {
        if (boardId == null || boardId.equals("")) {
            return APIResult.createNg("id不能为空");
        }
        Board board;
        try {
            board = boardMapper.getById(boardId);
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
        if (board == null) {
            return APIResult.createNg("板块不存在");
        }
        List<User> list = boardMapper.getHostList(boardId);
        if (list == null) {
            return APIResult.createNg("查询主持人失败或该板块没有主持人");
        }
        return APIResult.createOk(list);
    }
}
