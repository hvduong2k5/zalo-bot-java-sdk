package io.github.hvduong2k5.zalobot.model.bot;

public class BotInfo {
    private String id;
    private String accountName;
    private String accountType;
    private boolean canJoinGroups;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public boolean isCanJoinGroups() { return canJoinGroups; }
    public void setCanJoinGroups(boolean canJoinGroups) { this.canJoinGroups = canJoinGroups; }
}

