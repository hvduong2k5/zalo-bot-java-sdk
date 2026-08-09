package io.github.hvduong2k5.zalobot.model.webhook;

public final class GetUpdatesRequest {
    private final Integer timeout; // optional

    private GetUpdatesRequest(Builder builder) {
        this.timeout = builder.timeout;
    }

    public Integer getTimeout() { return timeout; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer timeout;

        private Builder() {}

        public Builder timeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public GetUpdatesRequest build() {
            return new GetUpdatesRequest(this);
        }
    }
}
