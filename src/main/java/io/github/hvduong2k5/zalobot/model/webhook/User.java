package io.github.hvduong2k5.zalobot.model.webhook;

public class User {
    private String id;
    private String displayName;
    private boolean isBot;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public boolean isBot() { return isBot; }
    public void setBot(boolean bot) { isBot = bot; }
}
