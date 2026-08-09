package io.github.hvduong2k5.zalobot.exception;

/**
 * Thrown when webhook secret token verification fails.
 * <p>
 * The caller (web framework layer) should catch this and return HTTP 401 or 403
 * to the Zalo server.
 */
public class WebhookVerificationException extends RuntimeException {

    public WebhookVerificationException(String message) {
        super(message);
    }
}
