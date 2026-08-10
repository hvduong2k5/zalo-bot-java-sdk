package io.github.hvduong2k5.zalobot;

import io.github.hvduong2k5.zalobot.api.http.HttpClient;
import io.github.hvduong2k5.zalobot.api.http.HttpRequest;
import io.github.hvduong2k5.zalobot.api.http.HttpResponse;
import io.github.hvduong2k5.zalobot.api.json.JsonMapper;
import io.github.hvduong2k5.zalobot.dispatcher.WebhookDispatcher;
import io.github.hvduong2k5.zalobot.handler.UpdateHandler;
import io.github.hvduong2k5.zalobot.exception.ZaloApiException;
import io.github.hvduong2k5.zalobot.exception.ZaloException;
import io.github.hvduong2k5.zalobot.exception.ZaloHttpException;
import io.github.hvduong2k5.zalobot.internal.http.OkHttpAdapter;
import io.github.hvduong2k5.zalobot.internal.json.JacksonAdapter;
import io.github.hvduong2k5.zalobot.polling.ZaloPolling;
import io.github.hvduong2k5.zalobot.model.base.EmptyResponse;
import io.github.hvduong2k5.zalobot.model.base.ZaloApiResponse;
import io.github.hvduong2k5.zalobot.model.bot.BotInfo;
import io.github.hvduong2k5.zalobot.model.bot.GetMeResponse;
import io.github.hvduong2k5.zalobot.model.message.SendChatActionRequest;
import io.github.hvduong2k5.zalobot.model.message.SendMessageRequest;
import io.github.hvduong2k5.zalobot.model.message.SendMessageResponse;
import io.github.hvduong2k5.zalobot.model.message.SendMessageResult;
import io.github.hvduong2k5.zalobot.model.message.SendPhotoRequest;
import io.github.hvduong2k5.zalobot.model.message.SendStickerRequest;
import io.github.hvduong2k5.zalobot.model.message.SendVoiceRequest;
import io.github.hvduong2k5.zalobot.model.polling.GetUpdatesRequest;
import io.github.hvduong2k5.zalobot.model.polling.GetUpdatesResponse;
import io.github.hvduong2k5.zalobot.model.update.Update;
import io.github.hvduong2k5.zalobot.model.webhook.SetWebhookRequest;
import io.github.hvduong2k5.zalobot.model.webhook.WebhookInfo;
import io.github.hvduong2k5.zalobot.model.webhook.WebhookInfoResponse;
import io.github.hvduong2k5.zalobot.util.Preconditions;

import java.io.IOException;

/**
 * Main entry point for interacting with the Zalo Bot Platform API.
 */
public final class ZaloBotClient {

    private static final String BASE_URL = "https://bot-api.zaloplatforms.com/bot";

    private final String botToken;
    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;

    private ZaloBotClient(Builder builder) {
        this.botToken = builder.botToken;
        this.httpClient = builder.httpClient != null ? builder.httpClient : new OkHttpAdapter();
        this.jsonMapper = builder.jsonMapper != null ? builder.jsonMapper : new JacksonAdapter();
    }

    // --- API Methods ---

    /**
     * Get information about the bot.
     * @return BotInfo object containing bot details.
     * @throws ZaloApiException if the API returns an error.
     */
    public BotInfo getMe() {
        GetMeResponse response = executeApi("/getMe", null, GetMeResponse.class);
        return response.getResult();
    }

    /**
     * Send a text message to a user.
     * @param request The SendMessageRequest containing the recipient and text.
     * @return SendMessageResult with the message ID.
     * @throws ZaloApiException if the API returns an error.
     */
    public SendMessageResult sendMessage(SendMessageRequest request) {
        Preconditions.checkNotNull(request, "request cannot be null");
        SendMessageResponse response = executeApi("/sendMessage", request, SendMessageResponse.class);
        return response.getResult();
    }

    /**
     * Send a photo to a user.
     * @param request The SendPhotoRequest containing the recipient and photo URL.
     * @return SendMessageResult with the message ID.
     * @throws ZaloApiException if the API returns an error.
     */
    public SendMessageResult sendPhoto(SendPhotoRequest request) {
        Preconditions.checkNotNull(request, "request cannot be null");
        SendMessageResponse response = executeApi("/sendPhoto", request, SendMessageResponse.class);
        return response.getResult();
    }

    /**
     * Send a sticker to a user.
     * @param request The SendStickerRequest containing the recipient and sticker ID.
     * @return SendMessageResult with the message ID.
     * @throws ZaloApiException if the API returns an error.
     */
    public SendMessageResult sendSticker(SendStickerRequest request) {
        Preconditions.checkNotNull(request, "request cannot be null");
        SendMessageResponse response = executeApi("/sendSticker", request, SendMessageResponse.class);
        return response.getResult();
    }

    /**
     * Send a voice message to a user.
     * @param request The SendVoiceRequest containing the recipient and voice URL.
     * @return SendMessageResult with the message ID.
     * @throws ZaloApiException if the API returns an error.
     */
    public SendMessageResult sendVoice(SendVoiceRequest request) {
        Preconditions.checkNotNull(request, "request cannot be null");
        SendMessageResponse response = executeApi("/sendVoice", request, SendMessageResponse.class);
        return response.getResult();
    }

    /**
     * Send a chat action (like "typing") to a user.
     * @param request The SendChatActionRequest containing the recipient and action.
     * @throws ZaloApiException if the API returns an error.
     */
    public void sendChatAction(SendChatActionRequest request) {
        Preconditions.checkNotNull(request, "request cannot be null");
        executeApi("/sendChatAction", request, EmptyResponse.class);
    }

    /**
     * Set a webhook URL to receive incoming updates.
     * @param request The SetWebhookRequest containing the URL.
     * @throws ZaloApiException if the API returns an error.
     */
    public void setWebhook(SetWebhookRequest request) {
        Preconditions.checkNotNull(request, "request cannot be null");
        executeApi("/setWebhook", request, EmptyResponse.class);
    }

    /**
     * Delete the previously set webhook.
     * @throws ZaloApiException if the API returns an error.
     */
    public void deleteWebhook() {
        executeApi("/deleteWebhook", null, EmptyResponse.class);
    }

    /**
     * Get information about the current webhook status.
     * @return WebhookInfo containing the webhook URL and status.
     * @throws ZaloApiException if the API returns an error.
     */
    public WebhookInfo getWebhookInfo() {
        WebhookInfoResponse response = executeApi("/getWebhookInfo", null, WebhookInfoResponse.class);
        return response.getResult();
    }

    /**
     * Retrieves the next pending update. Returns null if timeout expires. Maps to /getUpdates endpoint.
     */
    public Update getUpdate(GetUpdatesRequest request) {
        Preconditions.checkNotNull(request, "request cannot be null");
        GetUpdatesResponse response = executeApi("/getUpdates", request, GetUpdatesResponse.class);
        return response.getResult();
    }

    // --- Factory Methods ---


    /**
     * Creates a new WebhookDispatcher with a custom secret token.
     */
    public WebhookDispatcher newWebhookDispatcher(String secretToken, UpdateHandler handler) {
        return new WebhookDispatcher(secretToken, jsonMapper, handler);
    }

    /**
     * Creates a new polling engine to retrieve updates.
     */
    public ZaloPolling newPolling(UpdateHandler handler) {
        return new ZaloPolling(this, handler);
    }

    // --- Internal Execution ---

    private <T, R extends ZaloApiResponse<T>> R executeApi(String endpoint, Object requestBody, Class<R> responseClass) {
        String jsonBody = requestBody != null ? jsonMapper.toJson(requestBody) : null;
        String url = BASE_URL + botToken + endpoint;

        HttpRequest.Builder requestBuilder = HttpRequest.builder()
                .method("POST")
                .url(url);

        if (jsonBody != null) {
            requestBuilder.body(jsonBody);
        }

        HttpRequest request = requestBuilder.build();
        HttpResponse httpResponse;

        try {
            httpResponse = httpClient.execute(request);
        } catch (IOException e) {
            throw new ZaloHttpException("Network error while executing API: " + e.getMessage(), e);
        } catch (ZaloException e) {
            throw e;
        } catch (Exception e) {
            throw new ZaloException("Unexpected error during HTTP request execution", e);
        }

        if (!httpResponse.isSuccessful()) {
            throw new ZaloHttpException(httpResponse.getStatusCode(), httpResponse.getBody());
        }

        R response;
        try {
            response = jsonMapper.fromJson(httpResponse.getBody(), responseClass);
        } catch (ZaloException e) {
            throw e;
        } catch (Exception e) {
            throw new ZaloException("Failed to parse API response", e);
        }

        if (!response.isOk()) {
            throw new ZaloApiException(response.getErrorCode(), response.getDescription());
        }

        return response;
    }

    // --- Builder ---

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String botToken;
        private HttpClient httpClient;
        private JsonMapper jsonMapper;

        private Builder() {}

        public Builder botToken(String botToken) {
            this.botToken = botToken;
            return this;
        }

        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder jsonMapper(JsonMapper jsonMapper) {
            this.jsonMapper = jsonMapper;
            return this;
        }

        public ZaloBotClient build() {
            Preconditions.checkNotBlank(botToken, "botToken is required");
            return new ZaloBotClient(this);
        }
    }
}
