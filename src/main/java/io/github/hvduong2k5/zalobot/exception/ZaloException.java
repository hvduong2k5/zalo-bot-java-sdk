package io.github.hvduong2k5.zalobot.exception;

/**
 * Base exception for all Zalo SDK exceptions.
 */
public class ZaloException extends RuntimeException {

    public ZaloException(String message) {
        super(message);
    }

    public ZaloException(String message, Throwable cause) {
        super(message, cause);
    }
}
