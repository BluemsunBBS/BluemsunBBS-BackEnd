package ink.wyy.service;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Board;
import ink.wyy.bean.Pager;

public interface BoardService {

    APIResult insert(Board board);

    APIResult update(Board board);

    APIResult delete(String id);

    Pager<Board> getList(Pager<Board> pager);

    Pager<Board> find(String name, Pager<Board> pager);

    Board getById(String id);

    APIResult addHost(String userId, String boardId);

    boolean checkHost(String userId, String boardId);

    APIResult deleteHost(String userId, String boardId);

    APIResult getHostList(String boardId);
}
