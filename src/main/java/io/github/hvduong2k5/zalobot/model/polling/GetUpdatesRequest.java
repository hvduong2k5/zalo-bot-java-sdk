package io.github.hvduong2k5.zalobot.model.polling;

/**
 * Request parameters for the {@code /getUpdates} long-polling endpoint.
 * <p>
 * Unlike Telegram, Zalo does not use an {@code offset} parameter.
 * Each call returns the next pending update (single object).
 */
public final class GetUpdatesRequest {
    private final Integer timeout;

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
