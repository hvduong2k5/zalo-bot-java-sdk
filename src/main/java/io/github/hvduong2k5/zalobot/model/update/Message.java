package io.github.hvduong2k5.zalobot.model.update;

public class Message {
    private String messageId;
    private long date;
    private User from;
    private Chat chat;
    private String text;
    private String photo;
    private String photoUrl; // Zalo thực tế gửi "photo_url"
    private String caption;
    private String sticker;
    private String url;
    private String voiceUrl;
    private String messageType; // Zalo thực tế gửi "message_type"

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public User getFrom() { return from; }
    public void setFrom(User from) { this.from = from; }

    public Chat getChat() { return chat; }
    public void setChat(Chat chat) { this.chat = chat; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getPhoto() { 
        if (photo != null) return photo;
        return photoUrl;
    }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public String getSticker() { return sticker; }
    public void setSticker(String sticker) { this.sticker = sticker; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getVoiceUrl() { return voiceUrl; }
    public void setVoiceUrl(String voiceUrl) { this.voiceUrl = voiceUrl; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
}

