package io.github.hvduong2k5.zalobot.model.webhook;

import io.github.hvduong2k5.zalobot.util.Preconditions;

public final class SetWebhookRequest {
    private final String url;
    private final String secretToken;

    private SetWebhookRequest(Builder builder) {
        this.url = builder.url;
        this.secretToken = builder.secretToken;
    }

    public String getUrl() { return url; }
    public String getSecretToken() { return secretToken; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String url;
        private String secretToken;

        private Builder() {}

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder secretToken(String secretToken) {
            this.secretToken = secretToken;
            return this;
        }

        public SetWebhookRequest build() {
            Preconditions.checkNotBlank(url, "url is required");
            return new SetWebhookRequest(this);
        }
    }
}

