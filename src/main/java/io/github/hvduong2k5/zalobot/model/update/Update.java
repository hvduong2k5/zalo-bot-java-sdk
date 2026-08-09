package io.github.hvduong2k5.zalobot.model.update;

/**
 * Represents a single update event from the Zalo Bot API.
 * <p>
 * Shared by both Webhook and {@code /getUpdates} (long-polling) transport mechanisms.
 */
public class Update {
    private String eventName;
    private Message message;

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }
}
