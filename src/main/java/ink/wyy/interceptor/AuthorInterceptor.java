package ink.wyy.interceptor;

import ink.wyy.auth.AuthorAuth;
import ink.wyy.auth.HostAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.bean.Article;
import ink.wyy.bean.Board;
import ink.wyy.bean.User;
import ink.wyy.service.ArticleService;
import ink.wyy.service.BoardService;
import ink.wyy.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

public class AuthorInterceptor implements HandlerInterceptor {

    @Autowired
    private BoardService boardService;

    @Autowired
    private ArticleService articleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            AuthorAuth authorAuth = ((HandlerMethod) handler).getMethodAnnotation(AuthorAuth.class);
            if (authorAuth == null) {
                authorAuth = ((HandlerMethod) handler).getBean().getClass().getAnnotation(AuthorAuth.class);
            }
            if (authorAuth == null || !authorAuth.value()) {
                return true;
            } else {
                User user = (User) request.getAttribute("user");
                if (user.getRole() == 2) {
                    request.setAttribute("power", "admin");
                    return true;
                }
                Map pathVariables = (Map) request.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                String id = (String)pathVariables.get("articleId");
                Article article = articleService.getById(id, user.getId());
                if (article == null) {
                    PrintWriter writer = response.getWriter();
                    writer.print(JSONUtil.toJSONString(APIResult.createNg("帖子不存在")));
                    return false;
                }
                String boardId = article.getBoardId();
                Board board = boardService.getById(boardId);
                if (board == null) {
                    PrintWriter writer = response.getWriter();
                    writer.print(JSONUtil.toJSONString(APIResult.createNg("板块不存在")));
                    return false;
                }
                if (boardService.checkHost(user.getId(), boardId)) {
                    request.setAttribute("power", "host");
                    return true;
                }
                if (user.getId().equals(article.getUserId())) {
                    request.setAttribute("power", "user");
                    return true;
                }
                PrintWriter writer = response.getWriter();
                writer.print(JSONUtil.toJSONString(APIResult.createNg("您没有权限执行此操作")));
                return false;
            }
        } else {
            return true;
        }
    }
}
