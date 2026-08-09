package io.github.hvduong2k5.zalobot.model.request;

import io.github.hvduong2k5.zalobot.util.Preconditions;

public final class SendVoiceRequest {
    private final String chatId;
    private final String voice;

    private SendVoiceRequest(Builder builder) {
        this.chatId = builder.chatId;
        this.voice = builder.voice;
    }

    public String getChatId() { return chatId; }
    public String getVoice() { return voice; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String chatId;
        private String voice;

        private Builder() {}

        public Builder chatId(String chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder voice(String voice) {
            this.voice = voice;
            return this;
        }

        public SendVoiceRequest build() {
            Preconditions.checkNotBlank(chatId, "chatId is required");
            Preconditions.checkNotBlank(voice, "voice is required");
            return new SendVoiceRequest(this);
        }
    }
}
