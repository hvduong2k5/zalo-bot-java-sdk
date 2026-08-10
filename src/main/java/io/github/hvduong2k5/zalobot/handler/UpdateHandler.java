package io.github.hvduong2k5.zalobot.handler;

import io.github.hvduong2k5.zalobot.model.update.Update;

/**
 * Functional interface for handling a single incoming update from Zalo.
 */
@FunctionalInterface
public interface UpdateHandler {

    /**
     * Handles the given update.
     *
     * @param update the update to handle
     */
    void handle(Update update);
}
