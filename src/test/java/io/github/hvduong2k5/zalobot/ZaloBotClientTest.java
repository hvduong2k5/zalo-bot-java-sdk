package io.github.hvduong2k5.zalobot;

import io.github.hvduong2k5.zalobot.api.http.HttpClient;
import io.github.hvduong2k5.zalobot.api.http.HttpRequest;
import io.github.hvduong2k5.zalobot.api.http.HttpResponse;
import io.github.hvduong2k5.zalobot.api.json.JsonMapper;
import io.github.hvduong2k5.zalobot.exception.ZaloApiException;
import io.github.hvduong2k5.zalobot.exception.ZaloHttpException;
import io.github.hvduong2k5.zalobot.model.bot.BotInfo;
import io.github.hvduong2k5.zalobot.model.bot.GetMeResponse;
import io.github.hvduong2k5.zalobot.model.polling.GetUpdatesRequest;
import io.github.hvduong2k5.zalobot.model.polling.GetUpdatesResponse;
import io.github.hvduong2k5.zalobot.model.update.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ZaloBotClientTest {

    private ZaloBotClient client;
    private MockHttpClient mockHttpClient;
    private MockJsonMapper mockJsonMapper;

    @BeforeEach
    void setUp() {
        mockHttpClient = new MockHttpClient();
        mockJsonMapper = new MockJsonMapper();
        client = ZaloBotClient.builder()
                .botToken("test-token")
                .httpClient(mockHttpClient)
                .jsonMapper(mockJsonMapper)
                .build();
    }

    @Test
    void executeApi_success_returnsTypedResult() {
        // Arrange
        BotInfo botInfo = new BotInfo();
        GetMeResponse response = new GetMeResponse();
        response.setOk(true);
        response.setResult(botInfo);
        
        mockJsonMapper.nextResponse = response;
        mockHttpClient.nextResponse = new HttpResponse(200, "{}", null);

        // Act
        BotInfo result = client.getMe();

        // Assert
        assertSame(botInfo, result);
        assertNull(mockHttpClient.lastRequest.getBody(), "getMe should have no body");
        assertTrue(mockHttpClient.lastRequest.getUrl().endsWith("/getMe"));
    }

    @Test
    void executeApi_apiError_throwsZaloApiException() {
        // Arrange
        GetMeResponse response = new GetMeResponse();
        response.setOk(false);
        response.setErrorCode(400);
        response.setDescription("Bad Request");

        mockJsonMapper.nextResponse = response;
        mockHttpClient.nextResponse = new HttpResponse(200, "{}", null);

        // Act & Assert
        ZaloApiException ex = assertThrows(ZaloApiException.class, () -> client.getMe());
        assertEquals(400, ex.getErrorCode());
        assertEquals("Bad Request", ex.getDescription());
    }

    @Test
    void executeApi_httpError_throwsZaloHttpException() {
        // Arrange
        mockHttpClient.nextResponse = new HttpResponse(500, "Internal Server Error", null);

        // Act & Assert
        ZaloHttpException ex = assertThrows(ZaloHttpException.class, () -> client.getMe());
        assertEquals(500, ex.getStatusCode());
        assertEquals("Internal Server Error", ex.getResponseBody());
    }

    @Test
    void executeApi_networkError_throwsZaloHttpExceptionWithCause() {
        // Arrange
        mockHttpClient.nextException = new IOException("Connection reset");

        // Act & Assert
        ZaloHttpException ex = assertThrows(ZaloHttpException.class, () -> client.getMe());
        assertNull(ex.getStatusCode());
        assertTrue(ex.getCause() instanceof IOException);
        assertEquals("Connection reset", ex.getCause().getMessage());
    }

    @Test
    void getUpdate_withNullResult_returnsNull() {
        // Arrange
        GetUpdatesResponse response = new GetUpdatesResponse();
        response.setOk(true);
        response.setResult(null);

        mockJsonMapper.nextResponse = response;
        mockHttpClient.nextResponse = new HttpResponse(200, "{}", null);

        // Act
        Update result = client.getUpdate(GetUpdatesRequest.builder().build());

        // Assert
        assertNull(result);
    }
    
    @Test
    void executeApi_withBody_serializesBody() {
        // Arrange
        GetUpdatesResponse response = new GetUpdatesResponse();
        response.setOk(true);
        mockJsonMapper.nextResponse = response;
        mockHttpClient.nextResponse = new HttpResponse(200, "{}", null);
        mockJsonMapper.serializedJson = "{\"timeout\":30}";

        // Act
        client.getUpdate(GetUpdatesRequest.builder().timeout(30).build());

        // Assert
        assertEquals("{\"timeout\":30}", mockHttpClient.lastRequest.getBody());
    }

    // --- Mock Implementations ---

    private static class MockHttpClient implements HttpClient {
        HttpResponse nextResponse;
        IOException nextException;
        HttpRequest lastRequest;

        @Override
        public HttpResponse execute(HttpRequest request) throws IOException {
            this.lastRequest = request;
            if (nextException != null) throw nextException;
            return nextResponse;
        }
    }

    private static class MockJsonMapper implements JsonMapper {
        Object nextResponse;
        String serializedJson = "{}";

        @Override
        public String toJson(Object obj) {
            return serializedJson;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T fromJson(String json, Class<T> clazz) {
            return (T) nextResponse;
        }
    }
}
