package io.github.hvduong2k5.zalobot.model.request;

import io.github.hvduong2k5.zalobot.util.Preconditions;

public final class SendStickerRequest {
    private final String chatId;
    private final String sticker;

    private SendStickerRequest(Builder builder) {
        this.chatId = builder.chatId;
        this.sticker = builder.sticker;
    }

    public String getChatId() { return chatId; }
    public String getSticker() { return sticker; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String chatId;
        private String sticker;

        private Builder() {}

        public Builder chatId(String chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder sticker(String sticker) {
            this.sticker = sticker;
            return this;
        }

        public SendStickerRequest build() {
            Preconditions.checkNotBlank(chatId, "chatId is required");
            Preconditions.checkNotBlank(sticker, "sticker is required");
            return new SendStickerRequest(this);
        }
    }
}
