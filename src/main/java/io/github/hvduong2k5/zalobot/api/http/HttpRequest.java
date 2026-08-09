package io.github.hvduong2k5.zalobot.api.http;

import io.github.hvduong2k5.zalobot.util.Preconditions;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Immutable HTTP request object.
 * <p>
 * Use {@link #builder()} to construct instances.
 *
 * <pre>{@code
 * HttpRequest request = HttpRequest.builder()
 *     .method("POST")
 *     .url("https://bot-api.zaloplatforms.com/bot.../sendMessage")
 *     .header("Content-Type", "application/json")
 *     .body("{\"chat_id\":\"...\",\"text\":\"Hello\"}")
 *     .build();
 * }</pre>
 */
public final class HttpRequest {

    private final String method;
    private final String url;
    private final Map<String, String> headers;
    private final String body; // nullable for GET requests

    private HttpRequest(Builder builder) {
        this.method = builder.method;
        this.url = builder.url;
        
        Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.putAll(builder.headers);
        this.headers = Collections.unmodifiableMap(map);
        
        this.body = builder.body;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Returns the request body, or {@code null} for bodyless requests (e.g. GET).
     */
    public String getBody() {
        return body;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String method;
        private String url;
        private final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        private String body;

        private Builder() {
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * Adds a single header. Can be called multiple times.
         * Duplicate headers will be merged with a comma separator.
         */
        public Builder header(String name, String value) {
            Preconditions.checkNotBlank(name, "Header name cannot be blank");
            if (value != null) {
                this.headers.merge(name, value, (oldVal, newVal) -> oldVal + "," + newVal);
            }
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            Preconditions.checkNotBlank(method, "method must not be blank");
            Preconditions.checkNotBlank(url, "url must not be blank");
            return new HttpRequest(this);
        }
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

