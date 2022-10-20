package ink.wyy.service;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Notification;
import ink.wyy.bean.Pager;

public interface NotificationService {

    Pager<Notification> getByUser(String userId, String category, Pager<Notification> pager);

    APIResult insert(Notification n);

    APIResult readByUser(String userId, String category);

    APIResult readById(String id, boolean state);

    boolean deleteById(String id);

    int count(String user);

    int count(String user, boolean read);
}
