package ink.wyy.interceptor;

import ink.wyy.auth.HostAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.bean.Board;
import ink.wyy.bean.User;
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

public class HostInterceptor implements HandlerInterceptor {

    @Autowired
    private BoardService boardService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HostAuth hostAuth = ((HandlerMethod) handler).getMethodAnnotation(HostAuth.class);
            if (hostAuth == null) {
                hostAuth = ((HandlerMethod) handler).getBean().getClass().getAnnotation(HostAuth.class);
            }
            if (hostAuth == null || !hostAuth.value()) {
                return true;
            } else {
                User user = (User) request.getAttribute("user");
                Map pathVariables = (Map) request.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                String id = (String)pathVariables.get("boardId");
                Board board = boardService.getById(id);
                if (board == null) {
                    PrintWriter writer = response.getWriter();
                    writer.print(JSONUtil.toJSONString(APIResult.createNg("板块不存在")));
                    return false;
                }
                if (user.getRole() == 2 || boardService.checkHost(user.getId(), id)) {
                    return true;
                } else {
                    PrintWriter writer = response.getWriter();
                    writer.print(JSONUtil.toJSONString(APIResult.createNg("您没有权限执行此操作")));
                    return false;
                }
            }
        } else {
            return true;
        }
    }
}
