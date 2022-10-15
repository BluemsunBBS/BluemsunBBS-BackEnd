package ink.wyy.interceptor;

import ink.wyy.auth.ReplyerAuth;
import ink.wyy.bean.*;
import ink.wyy.service.ArticleService;
import ink.wyy.service.BoardService;
import ink.wyy.service.ReplyService;
import ink.wyy.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

public class ReplyerInterceptor implements HandlerInterceptor {

    @Autowired
    private ReplyService replyService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private BoardService boardService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            ReplyerAuth replyerAuth = ((HandlerMethod) handler).getMethodAnnotation(ReplyerAuth.class);
            if (replyerAuth == null) {
                replyerAuth = ((HandlerMethod) handler).getBean().getClass().getAnnotation(ReplyerAuth.class);
            }
            if (replyerAuth == null || !replyerAuth.value()) {
                return true;
            } else {
                User user = (User) request.getAttribute("user");
                if (user.getRole() == 2) {
                    return true;
                }
                Map pathVariables = (Map) request.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                String id = (String)pathVariables.get("replyId");
                Reply reply = replyService.getById(id);
                if (reply == null) {
                    PrintWriter writer = response.getWriter();
                    writer.print(JSONUtil.toJSONString(APIResult.createNg("评论不存在")));
                    return false;
                }
                if (user.getId().equals(reply.getUserId())) {
                    return true;
                }
                Article article = articleService.getById(reply.getArticleId());
                if (user.getId().equals(article.getUserId())) {
                    return true;
                }
                String boardId = article.getBoardId();
                Board board = boardService.getById(boardId);
                if (board == null) {
                    PrintWriter writer = response.getWriter();
                    writer.print(JSONUtil.toJSONString(APIResult.createNg("板块不存在")));
                    return false;
                }
                if (boardService.checkHost(user.getId(), boardId)) {
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
