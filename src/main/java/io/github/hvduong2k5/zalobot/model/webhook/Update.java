package io.github.hvduong2k5.zalobot.model.webhook;

public class Update {
    private String eventName;
    private Message message;

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }
}
