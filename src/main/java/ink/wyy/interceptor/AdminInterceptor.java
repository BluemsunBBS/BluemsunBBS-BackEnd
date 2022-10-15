package ink.wyy.interceptor;

import ink.wyy.auth.AdminAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.bean.User;
import ink.wyy.util.JSONUtil;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            AdminAuth adminAuth = ((HandlerMethod) handler).getMethodAnnotation(AdminAuth.class);
            if (adminAuth == null) {
                adminAuth = ((HandlerMethod) handler).getBean().getClass().getAnnotation(AdminAuth.class);
            }
            if (adminAuth == null || !adminAuth.value()) {
                return true;
            } else {
                User user = (User) request.getAttribute("user");
                if (user.getRole() == 2) { // root
                    return true;
                } else {
                    PrintWriter writer = response.getWriter();
                    writer.print(JSONUtil.toJSONString(APIResult.createNg("无操作权限")));
                    return false;
                }
            }
        } else {
            return true;
        }
    }
}
