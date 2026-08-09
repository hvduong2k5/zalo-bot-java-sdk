package io.github.hvduong2k5.zalobot.model.message;

import io.github.hvduong2k5.zalobot.util.Preconditions;

public final class SendChatActionRequest {
    private final String chatId;
    private final String action;

    private SendChatActionRequest(Builder builder) {
        this.chatId = builder.chatId;
        this.action = builder.action;
    }

    public String getChatId() { return chatId; }
    public String getAction() { return action; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String chatId;
        private String action;

        private Builder() {}

        public Builder chatId(String chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public SendChatActionRequest build() {
            Preconditions.checkNotBlank(chatId, "chatId is required");
            Preconditions.checkNotBlank(action, "action is required");
            return new SendChatActionRequest(this);
        }
    }
}

