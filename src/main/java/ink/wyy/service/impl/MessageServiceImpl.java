package ink.wyy.service.impl;

import ink.wyy.bean.Message;
import ink.wyy.mapper.MessageMapper;
import ink.wyy.service.MessageService;
import ink.wyy.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Autowired
    public MessageServiceImpl(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public List<Message> getByToUser(String userId) {
        if (userId == null || userId.equals("")) {
            return null;
        }
        return messageMapper.getByToUser(userId);
    }

    @Override
    public boolean sendMessage(Message message) {
        if (message.getToUser() == null
            || message.getFromUser() == null
            || message.getText() == null) {
            return false;
        }
        message.setId(UUIDUtil.get());
        return messageMapper.insert(message) == 1;
    }

    @Override
    public boolean deleteMessage(String id) {
        if (id == null || id.equals("")) {
            return false;
        }
        return messageMapper.delete(id) == 1;
    }

    @Override
    public Message getById(String id) {
        if (id == null || id.equals("")) {
            return null;
        }
        return messageMapper.getById(id);
    }
}
