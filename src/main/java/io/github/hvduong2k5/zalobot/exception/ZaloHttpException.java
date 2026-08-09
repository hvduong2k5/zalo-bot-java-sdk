package io.github.hvduong2k5.zalobot.exception;

/**
 * Thrown when an HTTP transport-level error occurs.
 * <p>
 * This covers two distinct scenarios:
 * <ul>
 *   <li><b>HTTP error response</b> (4xx/5xx) — has {@code statusCode} and {@code responseBody}</li>
 *   <li><b>Network failure</b> (timeout, connection refused) — has {@code cause}, no status code</li>
 * </ul>
 */
public class ZaloHttpException extends RuntimeException {

    private final Integer statusCode;
    private final String responseBody;

    /**
     * Constructor for HTTP error responses (non-2xx status with a body).
     *
     * @param statusCode   the HTTP status code
     * @param responseBody the raw response body, may be {@code null}
     */
    public ZaloHttpException(Integer statusCode, String responseBody) {
        super("HTTP error " + statusCode);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Constructor for network-level failures (timeout, connection refused, etc.).
     * <p>
     * Preserves the original cause (e.g. {@code SocketTimeoutException}) for stack trace debugging.
     *
     * @param message description of what went wrong
     * @param cause   the underlying exception
     */
    public ZaloHttpException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = null;
        this.responseBody = null;
    }

    /**
     * Returns the HTTP status code, or {@code null} for network-level failures (timeout, etc.).
     * <p>
     * {@code Integer} (not {@code int}) because timeouts never produce a real status code.
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the raw response body, or {@code null} for network-level failures.
     */
    public String getResponseBody() {
        return responseBody;
    }
}
