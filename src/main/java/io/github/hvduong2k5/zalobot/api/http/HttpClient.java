package io.github.hvduong2k5.zalobot.api.http;

import java.io.IOException;

/**
 * Contract for executing HTTP requests.
 * <p>
 * The default implementation uses OkHttp ({@code internal.http.OkHttpAdapter}),
 * but users may provide a custom implementation via {@code ZaloBotClient.builder().httpClient(...)}.
 */
public interface HttpClient {

    /**
     * Executes an HTTP request and returns the response.
     *
     * @param request the request to execute
     * @return the HTTP response
     * @throws IOException if a network error occurs (timeout, connection refused, etc.)
     */
    HttpResponse execute(HttpRequest request) throws IOException;
}
