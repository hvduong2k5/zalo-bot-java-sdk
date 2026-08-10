package io.github.hvduong2k5.zalobot.model.message;

import io.github.hvduong2k5.zalobot.util.Preconditions;

public final class SendVoiceRequest {
    private final String chatId;
    private final String voiceUrl;

    private SendVoiceRequest(Builder builder) {
        this.chatId = builder.chatId;
        this.voiceUrl = builder.voiceUrl;
    }

    public String getChatId() { return chatId; }
    public String getVoiceUrl() { return voiceUrl; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String chatId;
        private String voiceUrl;

        private Builder() {}

        public Builder chatId(String chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder voiceUrl(String voiceUrl) {
            this.voiceUrl = voiceUrl;
            return this;
        }

        public SendVoiceRequest build() {
            Preconditions.checkNotBlank(chatId, "chatId is required");
            Preconditions.checkNotBlank(voiceUrl, "voiceUrl is required");
            if (!voiceUrl.toLowerCase().endsWith(".aac")) {
                throw new IllegalArgumentException("Voice URL must have a .aac extension per Zalo API requirements.");
            }
            return new SendVoiceRequest(this);
        }
    }
}

