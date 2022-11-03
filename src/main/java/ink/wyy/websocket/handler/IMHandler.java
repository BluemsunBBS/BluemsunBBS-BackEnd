package ink.wyy.websocket.handler;

import ink.wyy.bean.Message;
import ink.wyy.service.MessageService;
import ink.wyy.service.impl.MessageServiceImpl;
import ink.wyy.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IMHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private static MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        IMHandler.messageService = messageService;
    }

    private String getUserId(WebSocketSession session) {
        try {
            String userId = (String) session.getAttributes().get("userId");
            return userId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserId(session);
        if (userId == null || userId.equals("")) {
            return;
        }
        sessionMap.put(userId, session);
        session.sendMessage(new TextMessage("已连接到服务器"));
        sendCacheMessage(userId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Message msg;
        try {
            msg = JSONUtil.parseObject((String) message.getPayload(), Message.class);
        } catch (Exception e) {
            session.sendMessage(new TextMessage("格式错误"));
            return;
        }
        if (msg == null) {
            session.sendMessage(new TextMessage("格式错误"));
            return;
        }
        String fromUser = getUserId(session);
        msg.setFromUser(fromUser);
        if (!sendMessageToUser(msg)) {
            messageService.sendMessage(msg);
        }
        session.sendMessage(new TextMessage("发送成功"));
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = this.getUserId(session);
        if (userId == null || userId.equals("")) {
            sessionMap.remove(userId);
            System.err.println("该" + userId + "用户已成功关闭");
        } else {
            System.err.println("关闭时，获取用户id为空");
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public boolean sendMessageToUser(Message msg) {
        String userId = msg.getToUser();
        WebSocketSession session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                TextMessage message = new TextMessage(JSONUtil.toJSONString(msg));
                session.sendMessage(message);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean sendMessagesToUser(List<Message> msg) {
        String userId = msg.get(0).getToUser();
        WebSocketSession session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                TextMessage message = new TextMessage(JSONUtil.toJSONString(msg));
                session.sendMessage(message);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean sendCacheMessage(String userId) {
        System.out.println(userId);
        List<Message> list = messageService.getByToUser(userId);
        if (list == null) return false;
        return sendMessagesToUser(list);
    }
}
