package io.github.hvduong2k5.zalobot.polling;

import io.github.hvduong2k5.zalobot.ZaloBotClient;
import io.github.hvduong2k5.zalobot.exception.ZaloApiException;
import io.github.hvduong2k5.zalobot.handler.UpdateHandler;
import io.github.hvduong2k5.zalobot.exception.ZaloHttpException;
import io.github.hvduong2k5.zalobot.model.polling.GetUpdatesRequest;
import io.github.hvduong2k5.zalobot.model.update.Update;

import java.util.Objects;

/**
 * Polling engine for retrieving updates from Zalo Bot Platform.
 * <p>
 * This engine runs in a background daemon thread, continuously fetching updates
 * using long polling. It handles network failures and temporary server errors
 * with an exponential backoff.
 * <p>
 * API-level errors stop the polling engine and are not automatically retried.
 */
public final class ZaloPolling {

    private static final int INITIAL_BACKOFF_MS = 1000;
    private static final int MAX_BACKOFF_MS = 30_000;
    private static final int POLLING_TIMEOUT_SECONDS = 30;

    private final ZaloBotClient client;
    private final UpdateHandler handler;
    private final Sleeper sleeper;

    private volatile boolean isRunning = false;
    private Thread pollingThread;

    /**
     * Creates a new ZaloPolling engine with the default sleeper.
     */
    public ZaloPolling(ZaloBotClient client, UpdateHandler handler) {
        this(client, handler, Thread::sleep);
    }

    /**
     * Creates a new ZaloPolling engine with a custom sleeper (for testing).
     */
    ZaloPolling(ZaloBotClient client, UpdateHandler handler, Sleeper sleeper) {
        this.client = Objects.requireNonNull(client, "client must not be null");
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
        this.sleeper = Objects.requireNonNull(sleeper, "sleeper must not be null");
    }

    /**
     * Starts the polling engine in a background daemon thread.
     * <p>
     * Calling start() immediately after stop() may fail if the previous polling
     * thread has not terminated yet.
     *
     * @throws IllegalStateException if the polling engine is already running
     *                               (previous thread has not terminated yet)
     */
    public synchronized void start() {
        if (pollingThread != null && pollingThread.isAlive()) {
            throw new IllegalStateException("Polling is already running (previous thread has not terminated yet)");
        }
        isRunning = true;
        pollingThread = new Thread(this::pollLoop, "ZaloPolling-Thread");
        pollingThread.setDaemon(true);
        pollingThread.start();
    }

    /**
     * Stops the polling engine.
     * <p>
     * Interrupts the polling thread, but cannot cancel an ongoing HTTP request.
     * The thread will terminate shortly after the current long-poll completes or
     * fails.
     */
    public synchronized void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
        if (pollingThread != null) {
            pollingThread.interrupt();
        }
    }

    private void pollLoop() {
        try {
            long backoffMs = INITIAL_BACKOFF_MS;
            GetUpdatesRequest request = GetUpdatesRequest.builder().timeout(POLLING_TIMEOUT_SECONDS).build();

            while (isRunning) {
                try {
                    Update update = client.getUpdate(request);

                    // Reset backoff on any successful response
                    backoffMs = INITIAL_BACKOFF_MS;

                    if (update != null) {
                        try {
                            handler.handle(update);
                        } catch (Exception handlerEx) {
                            // Exceptions thrown by UpdateHandler are caught and reported to stderr.
                            // Future versions will use SLF4J.
                            System.err.println("[ZaloPolling] UpdateHandler threw exception: " + handlerEx);
                            handlerEx.printStackTrace(System.err);
                        }
                    }
                } catch (ZaloApiException e) {
                    // Non-recoverable API error (ok=false) — stop polling
                    System.err.println("[ZaloPolling] Fatal API error, stopping polling: " + e);
                    e.printStackTrace(System.err);
                    isRunning = false;
                    break;
                } catch (ZaloHttpException e) {
                    if (isRetryable(e)) {
                        if (!sleepInterruptibly(backoffMs)) {
                            break;
                        }
                        backoffMs = Math.min(backoffMs * 2, MAX_BACKOFF_MS);
                    } else {
                        // Non-recoverable HTTP error (400, 401, 403, 404) — stop polling
                        System.err.println("[ZaloPolling] Non-retryable HTTP error, stopping polling: " + e);
                        e.printStackTrace(System.err);
                        isRunning = false;
                        break;
                    }
                }
            }
        } catch (RuntimeException e) {
            System.err.println("[ZaloPolling] Unexpected error, stopping polling: " + e);
            e.printStackTrace(System.err);
            isRunning = false;
        } finally {
            synchronized (this) {
                isRunning = false;
                pollingThread = null;
            }
        }
    }

    /**
     * Determines if an HTTP error is retryable.
     *
     * <p>
     * Retries on request timeouts (408), rate limiting (429),
     * and server errors (5xx). Network errors without an HTTP status
     * code are also considered retryable.
     *
     * <p>
     * Stops on other HTTP client errors.
     */
    private static boolean isRetryable(ZaloHttpException e) {
        Integer statusCode = e.getStatusCode();
        if (statusCode == null) {
            return true; // network error (timeout, connection refused)
        }
        return statusCode == 408 || statusCode == 429 || statusCode >= 500;
    }

    private boolean sleepInterruptibly(long ms) {
        try {
            sleeper.sleep(ms);
            return true;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            isRunning = false;
            return false;
        }
    }

    // --- Package-private interfaces for testing ---

    @FunctionalInterface
    interface Sleeper {
        void sleep(long ms) throws InterruptedException;
    }
}
