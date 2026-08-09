package io.github.hvduong2k5.zalobot.api.http;

import java.util.Collections;
import java.util.Map;

/**
 * Immutable HTTP response object.
 */
public final class HttpResponse {

    private final int statusCode;
    private final String body;
    private final Map<String, String> headers;

    public HttpResponse(int statusCode, String body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers != null
                ? Collections.unmodifiableMap(headers)
                : Collections.<String, String>emptyMap();
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the response body, may be {@code null} or empty.
     */
    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Returns {@code true} if the status code is in the 2xx range.
     */
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", bodyLength=" + (body != null ? body.length() : 0) +
                '}';
    }
}

