package io.github.hvduong2k5.zalobot.dispatcher;

import io.github.hvduong2k5.zalobot.api.json.JsonMapper;
import io.github.hvduong2k5.zalobot.exception.WebhookVerificationException;
import io.github.hvduong2k5.zalobot.model.update.Update;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Dispatcher for handling incoming webhook events from Zalo.
 * <p>
 * Performs constant-time secret token verification and routes events to the appropriate handler.
 */
public class WebhookDispatcher {

    private final String expectedSecretToken;
    private final JsonMapper jsonMapper;
    private final UpdateHandler handler;

    /**
     * Creates a new WebhookDispatcher.
     *
     * @param expectedSecretToken the expected secret token, or {@code null} to skip verification
     * @param jsonMapper          the JSON mapper for deserialization
     * @param handler             the handler for parsed updates
     */
    public WebhookDispatcher(String expectedSecretToken, JsonMapper jsonMapper, UpdateHandler handler) {
        this.expectedSecretToken = expectedSecretToken;
        this.jsonMapper = jsonMapper;
        this.handler = handler;
    }

    /**
     * Processes an incoming webhook request.
     *
     * @param body              the raw JSON body
     * @param secretTokenHeader the value of the secret token header sent by Zalo
     * @throws WebhookVerificationException if the token is invalid or missing
     * @throws RuntimeException             if JSON parsing fails
     */
    public void dispatch(String body, String secretTokenHeader) {
        verifySecret(secretTokenHeader);

        Update update = jsonMapper.fromJson(body, Update.class);
        if (update != null) {
            handler.handle(update);
        }
    }

    private void verifySecret(String secretTokenHeader) {
        if (expectedSecretToken != null) {
            if (secretTokenHeader == null) {
                throw new WebhookVerificationException("Missing secret token header");
            }
            if (!isEqualConstantTime(expectedSecretToken, secretTokenHeader)) {
                throw new WebhookVerificationException("Invalid secret token");
            }
        }
    }

    /**
     * Constant-time string comparison using MessageDigest.isEqual.
     * Prevents timing attacks on the secret token.
     */
    private boolean isEqualConstantTime(String a, String b) {
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(aBytes, bBytes);
    }
}
