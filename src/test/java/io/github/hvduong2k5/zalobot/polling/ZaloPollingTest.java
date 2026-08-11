package io.github.hvduong2k5.zalobot.polling;

import io.github.hvduong2k5.zalobot.ZaloBotClient;
import io.github.hvduong2k5.zalobot.api.http.HttpClient;
import io.github.hvduong2k5.zalobot.api.http.HttpRequest;
import io.github.hvduong2k5.zalobot.api.http.HttpResponse;
import io.github.hvduong2k5.zalobot.api.json.JsonMapper;
import io.github.hvduong2k5.zalobot.handler.UpdateHandler;
import io.github.hvduong2k5.zalobot.model.polling.GetUpdatesResponse;
import io.github.hvduong2k5.zalobot.model.update.Update;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ZaloPollingTest {

    private MockHttpClient mockHttpClient;
    private MockJsonMapper mockJsonMapper;
    private ZaloBotClient client;
    private MockUpdateHandler mockHandler;
    private MockSleeper mockSleeper;
    private ZaloPolling polling;

    @BeforeEach
    void setUp() {
        mockHttpClient = new MockHttpClient();
        mockJsonMapper = new MockJsonMapper();
        client = ZaloBotClient.builder()
                .botToken("test")
                .httpClient(mockHttpClient)
                .jsonMapper(mockJsonMapper)
                .build();
        mockHandler = new MockUpdateHandler();
        mockSleeper = new MockSleeper();
        polling = new ZaloPolling(client, mockHandler, mockSleeper);
    }

    @AfterEach
    void tearDown() {
        polling.stop();
    }

    @Test
    void start_twice_throwsIllegalStateException() throws InterruptedException {
        // Ngăn thread chết ngay lập tức do NPE bằng cách giả lập lỗi mạng để nó vào vòng lặp retry
        mockHttpClient.nextException = new IOException("Network error");
        polling.start();
        
        assertThrows(IllegalStateException.class, () -> polling.start());
    }

    @Test
    void stop_beforeStart_doesNotThrow() {
        assertDoesNotThrow(() -> polling.stop());
    }

    @Test
    void pollLoop_successfulResponse_callsHandler() throws InterruptedException {
        Update update = new Update();
        GetUpdatesResponse response = new GetUpdatesResponse();
        response.setOk(true);
        response.setResult(update);

        mockJsonMapper.nextResponse = response;
        mockHttpClient.nextResponse = new HttpResponse(200, "{}", null);

        mockHttpClient.failAfterRequests = 1;
        mockHttpClient.failWithException = new RuntimeException("Stop loop");
        mockHttpClient.expectRequests(1);

        polling.start();
        mockHttpClient.awaitRequests();
        polling.stop();

        assertTrue(mockHandler.wasCalled);
        assertSame(update, mockHandler.handledUpdate);
    }

    @Test
    void pollLoop_nullResult_doesNotCallHandler() throws InterruptedException {
        GetUpdatesResponse response = new GetUpdatesResponse();
        response.setOk(true);
        response.setResult(null);

        mockJsonMapper.nextResponse = response;
        mockHttpClient.nextResponse = new HttpResponse(200, "{}", null);
        mockHttpClient.failAfterRequests = 1;
        mockHttpClient.failWithException = new RuntimeException("Stop loop");
        mockHttpClient.expectRequests(1);

        polling.start();
        mockHttpClient.awaitRequests();
        polling.stop();

        assertFalse(mockHandler.wasCalled);
    }

    @Test
    void pollLoop_apiError_stopsPolling() throws InterruptedException {
        GetUpdatesResponse response = new GetUpdatesResponse();
        response.setOk(false);
        response.setErrorCode(400);
        response.setDescription("Bad Request");

        mockJsonMapper.nextResponse = response;
        mockHttpClient.nextResponse = new HttpResponse(200, "{}", null);

        polling.start();
        Thread.sleep(100); // Give thread time to fail and stop

        assertDoesNotThrow(() -> polling.start(), "Polling should have stopped, allowing it to be started again");
    }

    @Test
    void pollLoop_handlerThrows_continuesPolling() throws InterruptedException {
        Update update = new Update();
        GetUpdatesResponse response = new GetUpdatesResponse();
        response.setOk(true);
        response.setResult(update);

        mockJsonMapper.nextResponse = response;
        mockHttpClient.nextResponse = new HttpResponse(200, "{}", null);

        mockHandler.throwOnHandle = new RuntimeException("Handler crashed");
        mockHttpClient.failAfterRequests = 2; // Should survive 1st crash
        mockHttpClient.failWithException = new RuntimeException("Stop loop");
        mockHttpClient.expectRequests(2);

        polling.start();
        mockHttpClient.awaitRequests();
        polling.stop();

        assertEquals(2, mockHandler.callCount);
    }

    @Test
    void pollLoop_networkError_retriesWithExponentialBackoff() throws InterruptedException {
        mockHttpClient.nextException = new IOException("Network timeout");
        mockHttpClient.failAfterRequests = 4;
        mockHttpClient.failWithException = new RuntimeException("Stop loop");
        mockHttpClient.expectRequests(4);

        polling.start();
        mockHttpClient.awaitRequests();
        polling.stop();

        List<Long> sleeps = mockSleeper.getSleeps();
        assertTrue(sleeps.size() >= 3, "Should have slept at least 3 times");
        assertEquals(Arrays.asList(1000L, 2000L, 4000L), sleeps.subList(0, 3));
    }
    
    @Test
    void pollLoop_serverError_retriesWithExponentialBackoff() throws InterruptedException {
        mockHttpClient.nextResponse = new HttpResponse(502, "Bad Gateway", null);
        mockHttpClient.failAfterRequests = 4;
        mockHttpClient.failWithException = new RuntimeException("Stop loop");
        mockHttpClient.expectRequests(4);

        polling.start();
        mockHttpClient.awaitRequests();
        polling.stop();

        List<Long> sleeps = mockSleeper.getSleeps();
        assertTrue(sleeps.size() >= 3, "Should have slept at least 3 times");
        assertEquals(Arrays.asList(1000L, 2000L, 4000L), sleeps.subList(0, 3));
    }

    @Test
    void pollLoop_clientError_stopsPolling() throws InterruptedException {
        mockHttpClient.nextResponse = new HttpResponse(401, "Unauthorized", null);

        polling.start();
        Thread.sleep(100);

        assertDoesNotThrow(() -> polling.start(), "Polling should have stopped, allowing it to be started again");
    }

    // --- Mocks ---

    private static class MockHttpClient implements HttpClient {
        HttpResponse nextResponse;
        IOException nextException;
        int requestCount = 0;
        int failAfterRequests = -1;
        RuntimeException failWithException;
        private CountDownLatch latch = new CountDownLatch(1);

        @Override
        public HttpResponse execute(HttpRequest request) throws IOException {
            requestCount++;
            latch.countDown();
            
            if (failAfterRequests != -1 && requestCount > failAfterRequests) {
                throw failWithException;
            }
            if (nextException != null) throw nextException;
            return nextResponse;
        }
        
        void expectRequests(int count) {
            latch = new CountDownLatch(count);
        }

        void awaitRequests() throws InterruptedException {
            if (latch != null) latch.await(2, TimeUnit.SECONDS);
        }
    }

    private static class MockJsonMapper implements JsonMapper {
        Object nextResponse;

        @Override
        public String toJson(Object obj) {
            return "{}";
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T fromJson(String json, Class<T> clazz) {
            return (T) nextResponse;
        }
    }

    private static class MockUpdateHandler implements UpdateHandler {
        boolean wasCalled = false;
        int callCount = 0;
        Update handledUpdate = null;
        RuntimeException throwOnHandle;

        @Override
        public void handle(Update update) {
            wasCalled = true;
            callCount++;
            handledUpdate = update;
            if (throwOnHandle != null) throw throwOnHandle;
        }
    }
    
    private static class MockSleeper implements ZaloPolling.Sleeper {
        private final List<Long> sleeps = new ArrayList<>();
        
        @Override
        public void sleep(long ms) {
            synchronized(sleeps) {
                sleeps.add(ms);
            }
        }
        
        public List<Long> getSleeps() {
            synchronized(sleeps) {
                return new ArrayList<>(sleeps);
            }
        }
    }
}
