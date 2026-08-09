package io.github.hvduong2k5.zalobot.dispatcher;

import io.github.hvduong2k5.zalobot.api.json.JsonMapper;
import io.github.hvduong2k5.zalobot.exception.WebhookVerificationException;
import io.github.hvduong2k5.zalobot.exception.ZaloJsonException;
import io.github.hvduong2k5.zalobot.model.update.Update;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

/**
 * Dispatcher for handling incoming webhook events from Zalo.
 * <p>
 * Performs constant-time secret token verification and routes events to the appropriate handler.
 */
public final class WebhookDispatcher {

    private final String expectedSecretToken;
    private final JsonMapper jsonMapper;
    private final UpdateHandler handler;

    /**
     * Creates a new WebhookDispatcher.
     *
     * @param expectedSecretToken the expected secret token; {@code null} disables verification and should only be used when webhook authentication is handled by the surrounding application
     * @param jsonMapper          the JSON mapper for deserialization
     * @param handler             the handler for parsed updates
     */
    public WebhookDispatcher(String expectedSecretToken,
            JsonMapper jsonMapper,
            UpdateHandler handler) {
        this.expectedSecretToken = expectedSecretToken;
        this.jsonMapper = Objects.requireNonNull(jsonMapper, "jsonMapper must not be null");
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
    }

    /**
     * Processes an incoming webhook request.
     *
     * @param body              the raw JSON body
     * @param secretTokenHeader the value of the secret token header sent by Zalo
     * @throws WebhookVerificationException if the token is invalid or missing
     * @throws ZaloJsonException            if the JSON body cannot be deserialized
     */
    public void dispatch(String body, String secretTokenHeader) {
        Objects.requireNonNull(body, "body must not be null");
        verifySecret(secretTokenHeader);

        Update update = jsonMapper.fromJson(body, Update.class);
        if (update == null) {
            throw new ZaloJsonException("Webhook JSON deserialized to null");
        }
        handler.handle(update);
    }

    private void verifySecret(String secretTokenHeader) {
        if (expectedSecretToken == null) {
            return;
        }

        if (secretTokenHeader == null) {
            throw new WebhookVerificationException("Missing secret token header");
        }

        if (!isEqualConstantTime(expectedSecretToken, secretTokenHeader)) {
            throw new WebhookVerificationException("Invalid secret token");
        }
    }

    /**
     * Compares two UTF-8 encoded strings using {@link MessageDigest#isEqual(byte[], byte[])}.
     * Uses a constant-time byte comparison to reduce timing side-channel leakage during secret comparison.
     */
    private static boolean isEqualConstantTime(String a, String b) {
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(aBytes, bBytes);
    }
}
