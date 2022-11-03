package ink.wyy.interceptor;


import ink.wyy.auth.LoginAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.bean.User;
import ink.wyy.service.UserService;
import ink.wyy.util.JSONUtil;
import ink.wyy.util.JWTUtil;
import ink.wyy.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            User user = null;
            String token = request.getHeader("token");
            if (token != null && !token.equals("")) {
                user = JWTUtil.parseToken(token, User.class);
                if (user != null) {
                    request.setAttribute("user", user);
                }
            }
            LoginAuth loginAuth = ((HandlerMethod) handler).getMethodAnnotation(LoginAuth.class);
            if (loginAuth == null) {
                loginAuth = ((HandlerMethod) handler).getBean().getClass().getAnnotation(LoginAuth.class);
            }
            if (loginAuth == null || !loginAuth.value()) {
                return true;
            } else {
                if (user != null) {
                    user = userService.getById(user.getId());
                    request.setAttribute("user", user);
                    return true;
                } else {
                    PrintWriter writer = response.getWriter();
                    writer.print(JSONUtil.toJSONString(APIResult.createNg("未登录")));
                    return false;
                }
            }
        } else {
            return true;
        }
    }
}
