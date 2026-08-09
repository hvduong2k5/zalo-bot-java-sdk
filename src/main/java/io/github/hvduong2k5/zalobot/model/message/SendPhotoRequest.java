package io.github.hvduong2k5.zalobot.model.message;

import io.github.hvduong2k5.zalobot.util.Preconditions;

public final class SendPhotoRequest {
    private final String chatId;
    private final String photo;
    private final String caption;

    private SendPhotoRequest(Builder builder) {
        this.chatId = builder.chatId;
        this.photo = builder.photo;
        this.caption = builder.caption;
    }

    public String getChatId() { return chatId; }
    public String getPhoto() { return photo; }
    public String getCaption() { return caption; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String chatId;
        private String photo;
        private String caption;

        private Builder() {}

        public Builder chatId(String chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder photo(String photo) {
            this.photo = photo;
            return this;
        }

        public Builder caption(String caption) {
            this.caption = caption;
            return this;
        }

        public SendPhotoRequest build() {
            Preconditions.checkNotBlank(chatId, "chatId is required");
            Preconditions.checkNotBlank(photo, "photo is required");
            return new SendPhotoRequest(this);
        }
    }
}
