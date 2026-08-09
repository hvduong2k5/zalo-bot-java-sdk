package io.github.hvduong2k5.zalobot.polling;

import io.github.hvduong2k5.zalobot.ZaloBotClient;
import io.github.hvduong2k5.zalobot.dispatcher.UpdateHandler;
import io.github.hvduong2k5.zalobot.exception.ZaloApiException;
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
 */
public class ZaloPolling {

    private static final int INITIAL_BACKOFF_MS = 1000;
    private static final int MAX_BACKOFF_MS = 30_000;

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
     *
     * @throws IllegalStateException if the polling engine is already running
     */
    public synchronized void start() {
        if (isRunning) {
            throw new IllegalStateException("Polling is already running");
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
     * The thread will terminate shortly after the current long-poll completes or fails.
     */
    public synchronized void stop() {
        isRunning = false;
        if (pollingThread != null) {
            pollingThread.interrupt();
            pollingThread = null;
        }
    }

    private void pollLoop() {
        long backoffMs = INITIAL_BACKOFF_MS;
        GetUpdatesRequest request = GetUpdatesRequest.builder().timeout(30).build();

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
                    sleepInterruptibly(backoffMs);
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
    }

    private boolean isRetryable(ZaloHttpException e) {
        Integer code = e.getStatusCode();
        if (code == null) {
            return true; // network error (timeout, connection refused)
        }
        return code == 408 || code == 429 || code >= 500;
    }

    private void sleepInterruptibly(long ms) {
        try {
            sleeper.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            isRunning = false;
        }
    }

    // --- Package-private interfaces for testing ---

    @FunctionalInterface
    interface Sleeper {
        void sleep(long ms) throws InterruptedException;
    }
}
