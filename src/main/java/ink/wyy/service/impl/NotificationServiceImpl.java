package ink.wyy.service.impl;

import ink.wyy.bean.APIResult;
import ink.wyy.bean.Notification;
import ink.wyy.bean.Pager;
import ink.wyy.mapper.NotificationMapper;
import ink.wyy.service.NotificationService;
import ink.wyy.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationServiceImpl(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @Override
    public Pager<Notification> getByUser(String userId, String category, Pager<Notification> pager) {
        if (pager.getSize() == 0) pager.setSize(20);
        if (pager.getPage() == 0) pager.setPage(1);
        switch (category) {
            case "system":
            case "reply":
            case "like":
            case "friend":
            case "im":
                break;
            default:
                return null;
        }
        try {
            List<Notification> list = notificationMapper.selectByUser(userId, category, pager);
            if (list == null) return null;
            pager.setRows(list);
            pager.setTotal(notificationMapper.count(userId));
            return pager;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public APIResult insert(Notification n) {
        if (n.getCategory() == null || n.getCategory().equals("")) {
            return APIResult.createNg("分类不能为空");
        }
        if (n.getText() == null) {
            return APIResult.createNg("正文不能为空");
        }
        if (n.getUserId() == null) {
            return APIResult.createNg("用户不能为空");
        }
        n.setId(UUIDUtil.get());
        try {
            if (notificationMapper.insert(n) == 1) {
                return APIResult.createOk("发送成功");
            }
            return APIResult.createNg("发送失败");
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    @Override
    public APIResult readByUser(String userId, String category) {
        try {
            if (notificationMapper.readByUser(userId, category) > 0) {
                return APIResult.createOk("成功设置为已读");
            } else {
                return APIResult.createNg("设置失败");
            }
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    @Override
    public APIResult readById(String id, boolean state) {
        try {
            if (notificationMapper.readById(id, state) == 1) {
                return APIResult.createOk("成功设置已读状态");
            } else {
                return APIResult.createNg("设置失败");
            }
        } catch (Exception e) {
            return APIResult.createNg(e.getMessage());
        }
    }

    @Override
    public boolean deleteById(String id) {
        try {
            return notificationMapper.delete(id) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int count(String userId) {
        if (userId == null || userId.equals("")) {
            return 0;
        }
        return notificationMapper.count(userId);
    }

    @Override
    public int count(String userId, boolean read) {
        if (userId == null || userId.equals("")) {
            return 0;
        }
        return notificationMapper.countByRead(userId, read);
    }
}
