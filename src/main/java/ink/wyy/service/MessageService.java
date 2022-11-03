package ink.wyy.service;

import ink.wyy.bean.Message;

import java.util.List;

public interface MessageService {

    List<Message> getByToUser(String userId);

    boolean sendMessage(Message message);

    boolean deleteMessage(String id);

    Message getById(String id);
}
