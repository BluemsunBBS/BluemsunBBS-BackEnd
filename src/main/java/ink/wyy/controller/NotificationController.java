package ink.wyy.controller;

import ink.wyy.auth.LoginAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.bean.Notification;
import ink.wyy.bean.Pager;
import ink.wyy.bean.User;
import ink.wyy.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/message")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/list")
    @LoginAuth
    public APIResult getListByUser(Pager<Notification> pager,
                                   HttpServletRequest request,
                                   String category) {
        if (category == null || category.equals("")) {
            return APIResult.createNg("分类不能为空");
        }
        User user = (User) request.getAttribute("user");
        pager = notificationService.getByUser(user.getId(), category, pager);
        if (pager == null) {
            return APIResult.createNg("获取失败");
        }
        notificationService.readByUser(user.getId(), category);
        return APIResult.createOk(pager);
    }
}
