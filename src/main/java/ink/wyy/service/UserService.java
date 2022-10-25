package ink.wyy.service;

import ink.wyy.bean.Pager;
import ink.wyy.bean.User;

public interface UserService {
    User update(User user);

    boolean insert(User user);

    Pager<User> getList(Pager<User> pager);

    Pager<User> searchByNickname(String key, Pager<User> pager);

    User getById(String id);
}
