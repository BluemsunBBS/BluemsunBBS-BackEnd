package ink.wyy.service.impl;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Pager;
import ink.wyy.bean.User;
import ink.wyy.mapper.UserMapper;
import ink.wyy.service.UserService;
import ink.wyy.util.JWTUtil;
import ink.wyy.util.MD5Util;
import ink.wyy.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public APIResult login(String username, String password) {
        System.out.println(username);
        System.out.println(password);
        User user;
        try {
            user = userMapper.findByUsername(username);
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
        if (user == null) return null;
        if (user.getPassword().equals(MD5Util.getMD5Str(password, "BluemsunBBS"))) {
            user.setPassword(null);
            String token = JWTUtil.createToken(user);
            return APIResult.createOk(token);
        }
        return APIResult.createNg("用户名或密码错误");
    }

    public APIResult register(User user) {
        if (user.getUsername() == null || user.getUsername().equals("")) {
            return APIResult.createNg("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().equals("")) {
            return APIResult.createNg("密码不能为空");
        }
        user.setId(UUIDUtil.get());
        user.setPassword(MD5Util.getMD5Str(user.getPassword(), "BluemsunBBS"));
        if (!insert(user)) {
            return APIResult.createNg("用户名已被使用");
        }
        try {
            user = userMapper.findByUsername(user.getUsername());
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
        String token = JWTUtil.createToken(user);
        return APIResult.createOk(token);
    }

    @Override
    public User update(User user) {
        User old;
        try {
            old = userMapper.findById(user.getId());
        } catch (Exception e) {
            return null;
        }
        if (old == null) {
            return null;
        }
        if (user.getNickname() != null) {
            old.setNickname(user.getNickname());
        }
        if (user.getPassword() != null) {
            old.setPassword(MD5Util.getMD5Str(user.getPassword(), "BluemsunBBS"));
        }
        if (user.getPhone() != null) {
            old.setPhone(user.getPhone());
        }
        if (user.getGender() != null) {
            old.setGender(user.getGender());
        }
        if (user.getRealname() != null) {
            old.setRealname(user.getRealname());
        }
        if (user.getRole() != 0) {
            old.setRole(user.getRole());
        }
        try {
            if (userMapper.updateById(old) == 1) {
                return old;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean insert(User user) {
        try {
            return userMapper.insert(user) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Pager<User> getList(Pager<User> pager) {
        if (pager.getPage() == 0) pager.setPage(1);
        if (pager.getSize() == 0) pager.setSize(20);
        try {
            List<User> list = userMapper.selectAll(pager);
            for (User user : list) {
                user.setPassword(null);
            }
            pager.setTotal(userMapper.count());
            pager.setRows(list);
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    public Pager<User> getBanList(Pager<User> pager) {
        if (pager.getPage() == 0) pager.setPage(1);
        if (pager.getSize() == 0) pager.setSize(20);
        try {
            List<User> list = userMapper.selectBan(pager);
            for (User user : list) {
                user.setPassword(null);
            }
            pager.setTotal(userMapper.countBan());
            pager.setRows(list);
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean ban(String id) {
        try {
            if (userMapper.ban(id) == 1) return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean cancelBan(String id) {
        try {
            if (userMapper.cancelBan(id) == 1) return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public User getById(String id) {
        try {
            User user = userMapper.findById(id);
            if (user == null) return null;
            user.setPassword(null);
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}
