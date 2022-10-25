package ink.wyy.controller;

import ink.wyy.auth.AuthorAuth;
import ink.wyy.auth.LoginAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.bean.Article;
import ink.wyy.bean.Pager;
import ink.wyy.bean.User;
import ink.wyy.service.ArticleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/article")
@LoginAuth
public class ArticleController {

    static Logger logger = LogManager.getLogger();
    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/")
    public APIResult createArticle(@RequestBody Article article,
                                   HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        article.setUserId(user.getId());
        return articleService.insert(article);
    }

    @DeleteMapping("/{articleId}")
    @AuthorAuth
    public APIResult deleteArticle(@PathVariable("articleId") String id) {
        return articleService.delete(id);
    }

    @PutMapping("/{articleId}")
    @AuthorAuth
    public APIResult updateArticle(@PathVariable("articleId") String id,
                                   @RequestBody Article article,
                                   HttpServletRequest request) {
        if (article.getTop() != null) {
            if (request.getAttribute("power").equals("user")) {
                return APIResult.createNg("您没有权限执行本操作");
            }
        }
        article.setId(id);
        return articleService.update(article);
    }

    @GetMapping("/{articleId}")
    @LoginAuth(value = false)
    public APIResult getArticle(@PathVariable("articleId") String id,
                                HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        String userId = "";
        if (user != null) {
            userId = user.getId();
        }
        Article article = articleService.getById(id, userId);
        if (article == null) {
            return APIResult.createNg("帖子不存在");
        }
        articleService.visit(id);
        return APIResult.createOk(article);
    }

    public APIResult getList(String boardId, Pager<Article> pager, String order, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        String userId = user != null ? user.getId() : "";
        pager = articleService.getList(boardId, pager, order, userId);
        if (pager == null) {
            return APIResult.createNg("获取列表失败");
        }
        return APIResult.createOk(pager);
    }

    @GetMapping("/list/{boardId}")
    @LoginAuth(value = false)
    public APIResult findByTitle(String title,
                                 @PathVariable("boardId") String boardId,
                                 Pager<Article> pager,
                                 String order,
                                 HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        String userId = user != null ? user.getId() : "";
        if (title == null || title.equals("")) {
            return getList(boardId, pager, order, request);
        }
        pager = articleService.findByTitle(title, boardId, pager, order, userId);
        if (pager == null) {
            return APIResult.createNg("搜索失败");
        }
        return APIResult.createOk(pager);
    }

    @GetMapping("/list")
    @LoginAuth(value = false)
    public APIResult findAll(String title, Pager<Article> pager, String order, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        String userId = user != null ? user.getId() : "";
        pager = articleService.findAll(title, pager, order, userId);
        if (pager == null) {
            return  APIResult.createNg("搜索失败");
        }
        return APIResult.createOk(pager);
    }
}
