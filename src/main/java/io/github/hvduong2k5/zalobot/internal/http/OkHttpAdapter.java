package io.github.hvduong2k5.zalobot.internal.http;

import io.github.hvduong2k5.zalobot.api.http.HttpClient;
import io.github.hvduong2k5.zalobot.api.http.HttpRequest;
import io.github.hvduong2k5.zalobot.api.http.HttpResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OkHttp implementation of {@link HttpClient}.
 * <p>
 * SDK users should not instantiate this directly.
 * It is created automatically by {@code ZaloBotClient.builder().build()}.
 */
public final class OkHttpAdapter implements HttpClient {

    /**
     * Hardcoded JSON media type — this SDK always communicates in JSON.
     * Using a compile-time constant avoids null-checks and content-type negotiation.
     */
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient okHttpClient;

    public OkHttpAdapter(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    /**
     * Creates an adapter with default OkHttpClient (30s timeouts).
     */
    public OkHttpAdapter() {
        this(new OkHttpClient());
    }

    @Override
    public HttpResponse execute(HttpRequest request) throws IOException {
        Request okRequest = buildOkRequest(request);

        Response okResponse = okHttpClient.newCall(okRequest).execute();
        try {
            return convertResponse(okResponse);
        } finally {
            okResponse.close();
        }
    }

    private Request buildOkRequest(HttpRequest request) {
        Request.Builder builder = new Request.Builder().url(request.getUrl());

        // Add headers
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        // Build body — SDK only sends POST with JSON body
        String method = request.getMethod();
        if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            String bodyStr = request.getBody();
            RequestBody body = RequestBody.create(
                    bodyStr != null ? bodyStr : "",
                    JSON
            );
            builder.method(method, body);
        } else {
            // GET or other bodyless methods
            builder.method(method, null);
        }

        return builder.build();
    }

    private HttpResponse convertResponse(Response okResponse) throws IOException {
        int statusCode = okResponse.code();

        ResponseBody responseBody = okResponse.body();
        String bodyStr = responseBody != null ? responseBody.string() : "";

        // Collect response headers
        Map<String, String> headers = new HashMap<>();
        for (String name : okResponse.headers().names()) {
            headers.put(name, okResponse.header(name));
        }

        return new HttpResponse(statusCode, bodyStr, headers);
    }
}
