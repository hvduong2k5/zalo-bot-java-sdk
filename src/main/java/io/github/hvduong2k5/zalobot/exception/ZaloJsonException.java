package io.github.hvduong2k5.zalobot.exception;

/**
 * Exception thrown when there is an error serializing or deserializing JSON.
 * <p>
 * This exception wraps the underlying JSON processing exceptions (e.g., Jackson's JsonProcessingException)
 * to hide the implementation details from the SDK users.
 */
public class ZaloJsonException extends RuntimeException {

    public ZaloJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}

