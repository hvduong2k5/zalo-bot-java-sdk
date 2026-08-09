package io.github.hvduong2k5.zalobot.internal.http;

import io.github.hvduong2k5.zalobot.api.http.HttpRequest;
import io.github.hvduong2k5.zalobot.api.http.HttpResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OkHttpAdapterTest {

    private MockWebServer server;
    private OkHttpAdapter adapter;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        adapter = new OkHttpAdapter();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void execute_successfulPostRequest() throws Exception {
        // Arrange
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"ok\":true}")
                .addHeader("X-Test-Header", "Value123"));

        HttpRequest request = HttpRequest.builder()
                .method("POST")
                .url(server.url("/api/test").toString())
                .header("Authorization", "Bearer 123")
                .body("{\"key\":\"value\"}")
                .build();

        // Act
        HttpResponse response = adapter.execute(request);

        // Assert
        assertTrue(response.isSuccessful());
        assertEquals(200, response.getStatusCode());
        assertEquals("{\"ok\":true}", response.getBody());
        assertEquals("Value123", response.getHeaders().get("X-Test-Header"));

        // Verify request sent correctly
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/api/test", recordedRequest.getPath());
        assertEquals("Bearer 123", recordedRequest.getHeader("Authorization"));
        assertEquals("application/json; charset=utf-8", recordedRequest.getHeader("Content-Type"));
        assertEquals("{\"key\":\"value\"}", recordedRequest.getBody().readUtf8());
    }

    @Test
    void execute_errorResponse() throws Exception {
        // Arrange
        server.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("Bad Request"));

        HttpRequest request = HttpRequest.builder()
                .method("GET")
                .url(server.url("/api/error").toString())
                .build();

        // Act
        HttpResponse response = adapter.execute(request);

        // Assert
        assertFalse(response.isSuccessful());
        assertEquals(400, response.getStatusCode());
        assertEquals("Bad Request", response.getBody());

        // Verify request sent correctly (GET shouldn't have body/content-type)
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
    }
}
