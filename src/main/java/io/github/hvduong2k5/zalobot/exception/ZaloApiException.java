package io.github.hvduong2k5.zalobot.exception;

/**
 * Thrown when the Zalo Bot API returns a business-level error ({@code ok = false}).
 * <p>
 * Example response:
 * <pre>{@code
 * { "ok": false, "error_code": 400, "description": "Bad Request: chat not found" }
 * }</pre>
 */
public class ZaloApiException extends ZaloException {

    private final Integer errorCode;
    private final String description;

    /**
     * @param errorCode   the Zalo error code, may be {@code null} if not present in response
     * @param description the error description from Zalo
     */
    public ZaloApiException(Integer errorCode, String description) {
        super(buildMessage(errorCode, description));
        this.errorCode = errorCode;
        this.description = description;
    }

    /**
     * Returns the Zalo error code, or {@code null} if the response did not include one.
     * <p>
     * This is {@code Integer} (not {@code int}) to avoid unboxing NPE when the field is absent.
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the error description from Zalo, may be {@code null}.
     */
    public String getDescription() {
        return description;
    }

    private static String buildMessage(Integer errorCode, String description) {
        StringBuilder sb = new StringBuilder("Zalo API error");
        if (errorCode != null) {
            sb.append(" [").append(errorCode).append("]");
        }
        if (description != null && !description.isEmpty()) {
            sb.append(": ").append(description);
        }
        return sb.toString();
    }
}

