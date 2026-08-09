package io.github.hvduong2k5.zalobot.dispatcher;

import io.github.hvduong2k5.zalobot.api.json.JsonMapper;
import io.github.hvduong2k5.zalobot.exception.WebhookVerificationException;
import io.github.hvduong2k5.zalobot.model.update.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebhookDispatcherTest {

    private MockJsonMapper mockJsonMapper;
    private MockUpdateHandler mockHandler;

    @BeforeEach
    void setUp() {
        mockJsonMapper = new MockJsonMapper();
        mockHandler = new MockUpdateHandler();
    }

    @Test
    void dispatch_withMatchingSecret_invokesHandler() {
        // Arrange
        WebhookDispatcher dispatcher = new WebhookDispatcher("secret123", mockJsonMapper, mockHandler);
        Update dummyUpdate = new Update();
        mockJsonMapper.nextResponse = dummyUpdate;

        // Act
        dispatcher.dispatch("{}", "secret123");

        // Assert
        assertTrue(mockHandler.wasCalled);
        assertSame(dummyUpdate, mockHandler.handledUpdate);
    }

    @Test
    void dispatch_withMismatchedSecret_throwsException() {
        // Arrange
        WebhookDispatcher dispatcher = new WebhookDispatcher("secret123", mockJsonMapper, mockHandler);

        // Act & Assert
        assertThrows(WebhookVerificationException.class, () -> {
            dispatcher.dispatch("{}", "wrong_secret");
        });
        assertFalse(mockHandler.wasCalled);
    }

    @Test
    void dispatch_withNullHeader_throwsException() {
        // Arrange
        WebhookDispatcher dispatcher = new WebhookDispatcher("secret123", mockJsonMapper, mockHandler);

        // Act & Assert
        assertThrows(WebhookVerificationException.class, () -> {
            dispatcher.dispatch("{}", null);
        });
        assertFalse(mockHandler.wasCalled);
    }

    @Test
    void dispatch_withNullBody_throwsException() {
        // Arrange
        WebhookDispatcher dispatcher = new WebhookDispatcher("secret123", mockJsonMapper, mockHandler);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            dispatcher.dispatch(null, "secret123");
        });
        assertFalse(mockHandler.wasCalled);
    }

    @Test
    void dispatch_withNoExpectedSecret_skipsVerificationAndInvokesHandler() {
        // Arrange
        WebhookDispatcher dispatcher = new WebhookDispatcher(null, mockJsonMapper, mockHandler);
        Update dummyUpdate = new Update();
        mockJsonMapper.nextResponse = dummyUpdate;

        // Act
        dispatcher.dispatch("{}", null); // Even if header is null, it should pass
        
        // Assert
        assertTrue(mockHandler.wasCalled);
        assertSame(dummyUpdate, mockHandler.handledUpdate);
    }

    @Test
    void dispatch_withNullUpdate_throwsException() {
        // Arrange
        WebhookDispatcher dispatcher = new WebhookDispatcher(null, mockJsonMapper, mockHandler);
        mockJsonMapper.nextResponse = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            dispatcher.dispatch("{}", null);
        });
        assertFalse(mockHandler.wasCalled);
    }

    // --- Mocks ---

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
        Update handledUpdate = null;

        @Override
        public void handle(Update update) {
            wasCalled = true;
            handledUpdate = update;
        }
    }
}
