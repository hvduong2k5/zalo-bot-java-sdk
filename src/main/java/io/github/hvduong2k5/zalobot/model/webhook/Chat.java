package io.github.hvduong2k5.zalobot.model.webhook;

public class Chat {
    private String id;
    private String chatType; // "PRIVATE" or "GROUP"

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChatType() { return chatType; }
    public void setChatType(String chatType) { this.chatType = chatType; }
}
