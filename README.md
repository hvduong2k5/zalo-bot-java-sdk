# Zalo Bot Java SDK

A lightweight, robust Java SDK for interacting with the Zalo Bot Platform API. This SDK provides an intuitive builder pattern, asynchronous capabilities, and supports both Webhook and Long Polling methods for receiving updates.

## Features
- **Lightweight:** Minimal dependencies (`okhttp`, `jackson`, and `slf4j`).
- **Comprehensive API Support:** Supports `getMe`, `sendMessage`, `sendPhoto`, `sendSticker`, `sendVoice`, `sendChatAction`, and Webhook configuration.
- **Dual Update Models:** Supports both Webhooks (for production) and Long Polling (for local development).
- **Strong Typing:** Provides full Java objects for Requests and Responses.

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.hvduong2k5</groupId>
    <artifactId>zalo-bot-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### 1. Initialize the Client

```java
import io.github.hvduong2k5.zalobot.ZaloBotClient;

public class Main {
    public static void main(String[] args) {
        ZaloBotClient client = ZaloBotClient.builder()
                .botToken("YOUR_ZALO_BOT_TOKEN")
                .build();
                
        System.out.println("Bot Name: " + client.getMe().getName());
    }
}
```

### 2. Sending Messages

You can easily send Text, Photos, Stickers, and Voices.

```java
import io.github.hvduong2k5.zalobot.model.message.*;

// Send Text
SendMessageRequest textReq = SendMessageRequest.builder()
    .chatId("USER_ID")
    .text("Hello from Zalo Bot Java SDK!")
    .build();
client.sendMessage(textReq);

// Send Photo
SendPhotoRequest photoReq = SendPhotoRequest.builder()
    .chatId("USER_ID")
    .photo("https://example.com/image.png")
    .caption("Look at this!")
    .build();
client.sendPhoto(photoReq);

// Send Voice
SendVoiceRequest voiceReq = SendVoiceRequest.builder()
    .chatId("USER_ID")
    .voiceUrl("https://example.com/audio.aac") // Must be .aac
    .build();
client.sendVoice(voiceReq);
```

### 3. Receiving Updates (Long Polling)

For local development or environments where you cannot expose an HTTP port, use Long Polling.

```java
import io.github.hvduong2k5.zalobot.handler.UpdateHandler;
import io.github.hvduong2k5.zalobot.model.update.Update;
import io.github.hvduong2k5.zalobot.polling.ZaloPolling;

UpdateHandler handler = new UpdateHandler() {
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Received message: " + update.getMessage().getText());
    }
};

ZaloPolling polling = client.newPolling(handler);
polling.start();

// Remember to stop it when shutting down
// polling.stop();
```

### 4. Receiving Updates (Webhook)

For production, you should use Webhooks. The SDK provides a `WebhookDispatcher` to easily parse incoming JSON payloads.

```java
import io.github.hvduong2k5.zalobot.dispatcher.WebhookDispatcher;
import io.github.hvduong2k5.zalobot.model.webhook.SetWebhookRequest;

// 1. Tell Zalo where to send updates
client.setWebhook(SetWebhookRequest.builder()
    .url("https://your-domain.com/api/webhook")
    .build());

// 2. Initialize Dispatcher with your Secret Token
WebhookDispatcher dispatcher = client.newWebhookDispatcher("YOUR_SECRET_TOKEN", handler);

// 3. Use it in your web server (e.g. Spring Boot Controller)
@PostMapping("/api/webhook")
public ResponseEntity<String> handleWebhook(
        @RequestHeader("X-Zalo-Signature") String signature,
        @RequestBody String jsonBody) {
        
    try {
        dispatcher.dispatch(signature, jsonBody);
        return ResponseEntity.ok("OK");
    } catch (Exception e) {
        return ResponseEntity.status(403).body("Invalid signature");
    }
}
```

### 5. Advanced: Handling Unknown or Custom Fields

If the Zalo platform adds new fields that are not yet available in the SDK, or if you want to use custom models (e.g., extending `Message`), you can provide a custom `JsonMapper`.

For example, using Jackson to map new fields into a custom subclass:

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.hvduong2k5.zalobot.internal.json.JacksonAdapter;
import io.github.hvduong2k5.zalobot.model.update.Message;

// 1. Create your custom model extending the SDK's model
public class MyCustomMessage extends Message {
    private String videoUrl;
    
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}

// 2. Configure Jackson to use your custom class
ObjectMapper mapper = new ObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

SimpleModule module = new SimpleModule();
module.addAbstractTypeMapping(Message.class, MyCustomMessage.class);
mapper.registerModule(module);

// 3. Inject the custom mapper into the client
ZaloBotClient client = ZaloBotClient.builder()
        .botToken("YOUR_ZALO_BOT_TOKEN")
        .jsonMapper(new JacksonAdapter(mapper))
        .build();
```

## Contributing
Contributions are welcome! Please run `mvn clean verify` to ensure all tests pass before submitting a pull request.
