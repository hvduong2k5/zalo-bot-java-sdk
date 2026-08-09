package io.github.hvduong2k5.zalobot.model.message;

import io.github.hvduong2k5.zalobot.util.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SendMessageRequest {
    private final String chatId;
    private final String text;
    private final String parseMode;
    private final List<TextStyle> textStyles;

    private SendMessageRequest(Builder builder) {
        this.chatId = builder.chatId;
        this.text = builder.text;
        this.parseMode = builder.parseMode;
        this.textStyles = builder.textStyles.isEmpty() 
            ? null 
            : Collections.unmodifiableList(new ArrayList<>(builder.textStyles));
    }

    public String getChatId() { return chatId; }
    public String getText() { return text; }
    public String getParseMode() { return parseMode; }
    public List<TextStyle> getTextStyles() { return textStyles; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String chatId;
        private String text;
        private String parseMode;
        private final List<TextStyle> textStyles = new ArrayList<>();

        private Builder() {}

        public Builder chatId(String chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder parseMode(String parseMode) {
            this.parseMode = parseMode;
            return this;
        }

        public Builder addTextStyle(TextStyle textStyle) {
            this.textStyles.add(Preconditions.checkNotNull(textStyle, "textStyle cannot be null"));
            return this;
        }
        
        public Builder textStyles(List<TextStyle> textStyles) {
            this.textStyles.clear();
            if (textStyles != null) {
                this.textStyles.addAll(textStyles);
            }
            return this;
        }

        public SendMessageRequest build() {
            Preconditions.checkNotBlank(chatId, "chatId is required");
            Preconditions.checkNotBlank(text, "text is required");
            Preconditions.checkArgument(text.length() <= 2000, "text exceeds 2000 characters limit");
            return new SendMessageRequest(this);
        }
    }
}

