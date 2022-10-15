package ink.wyy.controller;

import ink.wyy.auth.AdminAuth;
import ink.wyy.auth.HostAuth;
import ink.wyy.auth.LoginAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.bean.Board;
import ink.wyy.bean.Pager;
import ink.wyy.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@LoginAuth
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/board")
    @AdminAuth
    public APIResult createBoard(@RequestBody Board board) {
        return boardService.insert(board);
    }

    @PutMapping("/board/{boardId}")
    @HostAuth
    public APIResult updateBoard(@PathVariable("boardId") String id,
                                 @RequestBody Board board,
                                 HttpServletRequest request) {
        board.setId(id);
        return boardService.update(board);
    }

    @DeleteMapping("/board/{boardId}")
    @AdminAuth
    public APIResult deleteBoard(@PathVariable("boardId") String id) {
        if (id == null || id.equals("")) {
            return APIResult.createNg("id不能为空");
        }
        return boardService.delete(id);
    }

    @GetMapping("/board/{boardId}")
    @LoginAuth(value = false)
    public APIResult getBoard(@PathVariable("boardId") String id) {
        Board board = boardService.getById(id);
        if (board == null) {
            return APIResult.createNg("板块不存在");
        }
        return APIResult.createOk(board);
    }

    public APIResult getBoard(Pager<Board> pager) {
        pager = boardService.getList(pager);
        if (pager == null) {
            return APIResult.createNg("获取列表失败");
        }
        return APIResult.createOk(pager);
    }

    @GetMapping("/board/")
    @LoginAuth(value = false)
    public APIResult findBoard(String name, Pager<Board> pager) {
        if (name == null || name.equals("")) {
            return getBoard(pager);
        }
        pager = boardService.find(name, pager);
        if (pager == null) {
            return APIResult.createNg("查询失败");
        }
        return APIResult.createOk(pager);
    }

    @PutMapping("/board/host")
    @AdminAuth
    public APIResult addHost(@RequestBody Map<String, String> map) {
        return boardService.addHost(map.get("user_id"), map.get("board_id"));
    }

    @DeleteMapping("/board/host")
    @AdminAuth
    public APIResult deleteHost(@RequestBody Map<String, String> map) {
        return boardService.deleteHost(map.get("user_id"), map.get("board_id"));
    }

    @GetMapping("/board/host")
    @LoginAuth(value = false)
    public APIResult getHostList(@RequestParam("board_id") String boardId) {
        return boardService.getHostList(boardId);
    }
}
