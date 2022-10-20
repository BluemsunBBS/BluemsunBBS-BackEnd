package ink.wyy.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Notification {

    private String id;
    private String category;
    private String text;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("is_read")
    private boolean isRead;
    @JsonProperty("notice_time")
    private Date noticeTime;

    public Notification(String category, String text, String userId) {
        this.category = category;
        this.text = text;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean read) {
        isRead = read;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date getNoticeTime() {
        return noticeTime;
    }

    public void setNoticeTime(Date noticeTime) {
        this.noticeTime = noticeTime;
    }
}
