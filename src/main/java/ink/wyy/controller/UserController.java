package ink.wyy.controller;

import ink.wyy.auth.AdminAuth;
import ink.wyy.auth.LoginAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.bean.Pager;
import ink.wyy.bean.User;
import ink.wyy.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping("/account/{id}")
    public APIResult getUserInfo(@PathVariable("id") String id, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        user = userServiceImpl.getById(id);
        if (user == null) {
            return APIResult.createNg("用户不存在");
        }
        if (!user.getId().equals(id) && user.getRole() != 2) {
            user.setPhone(null);
            user.setRealname(null);
            user.setRole(0);
            user.setCreateTime(null);
            user.setUsername(null);
        }
        return APIResult.createOk(user);
    }

    @PutMapping("/account/{id}")
    @LoginAuth
    public APIResult putUserInfo(@PathVariable("id") String id, HttpServletRequest request,
                                 @RequestBody User cuser) {
        User user = (User) request.getAttribute("user");
        if (!user.getId().equals(id) && user.getRole() != 2) {
            return APIResult.createNg("您没有权限执行此操作");
        }
        if (cuser.getRole() != 0 && user.getRole() != 2) {
            return APIResult.createNg("您没有权限执行此操作");
        }
        if (cuser.getLevel() != 0 && user.getRole() != 2) {
            return APIResult.createNg("您没有权限执行此操作");
        }
        cuser.setId(id);
        user = userServiceImpl.update(cuser);
        if (user == null) {
            return APIResult.createNg("更新失败");
        }
        return APIResult.createOk("更新成功");
    }

    @GetMapping("/account/")
    @AdminAuth
    public APIResult getUserList(Pager<User> pager) {
        Pager<User> pager1 = userServiceImpl.getList(pager);
        if (pager1 != null) {
            return APIResult.createOk(pager1);
        } else {
            return APIResult.createNg("获取失败");
        }
    }

    @GetMapping("/account/search")
    public APIResult searchByNickname(String key, Pager<User> pager) {
        pager = userServiceImpl.searchByNickname(key, pager);
        if (pager != null) {
            return APIResult.createOk(pager);
        } else {
            return APIResult.createNg("获取失败");
        }
    }

    @PostMapping("/account/login")
    public APIResult login(@RequestBody User user) {
        APIResult result = userServiceImpl.login(user.getUsername(), user.getPassword());
        return result;
    }

    @PostMapping("/account/register")
    public APIResult register(@RequestBody User user) {
        return userServiceImpl.register(user);
    }

    @DeleteMapping("/account/ban/{id}")
    @AdminAuth
    public APIResult ban(@PathVariable("id") String id) {
        if (userServiceImpl.ban(id)) {
            return APIResult.createOk("封禁用户成功");
        } else {
            return APIResult.createNg("用户不存在或已被封禁");
        }
    }

    @PutMapping("/account/ban/{id}")
    @AdminAuth
    public APIResult cancelBan(@PathVariable("id") String id) {
        if (userServiceImpl.cancelBan(id)) {
            return APIResult.createOk("解除封禁成功");
        } else {
            return APIResult.createNg("用户不存在或未被封禁");
        }
    }

    @GetMapping("/account/ban/")
    @AdminAuth
    public APIResult getBanList(Pager<User> pager) {
        Pager<User> pager1 = userServiceImpl.getBanList(pager);
        if (pager1 != null) {
            return APIResult.createOk(pager1);
        } else {
            return APIResult.createNg("获取失败");
        }
    }
}
