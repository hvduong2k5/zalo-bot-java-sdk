package io.github.hvduong2k5.zalobot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.github.hvduong2k5.zalobot.model.bot.GetMeResponse;
import io.github.hvduong2k5.zalobot.model.polling.GetUpdatesResponse;
import io.github.hvduong2k5.zalobot.model.update.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for {@code ZaloApiResponse<T>} generic deserialization.
 * <p>
 * Validates that Jackson correctly resolves the generic type parameter {@code T}
 * via {@code getGenericSuperclass()} at class-definition time for all response subclasses.
 * <p>
 * Uses <strong>production classes only</strong> (no test doubles) to ensure this test
 * protects the real contract.
 */
public class JacksonGenericTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    /**
     * Test case: T = Update (single object, not a collection).
     * Verifies that getUpdates response correctly deserializes the result
     * as an Update object (not a LinkedHashMap).
     */
    @Test
    void getUpdatesResponse_deserializesToSingleUpdate() throws Exception {
        // Zalo returns a single Update object in "result", NOT an array
        String json = "{\"ok\":true,\"result\":{\"event_name\":\"message.text.received\",\"message\":{\"text\":\"Hello\"}}}";

        GetUpdatesResponse response = mapper.readValue(json, GetUpdatesResponse.class);

        assertTrue(response.isOk());
        assertNotNull(response.getResult());

        // Core assertion: result is a real Update, not a raw Map
        Update update = response.getResult();
        assertEquals("message.text.received", update.getEventName());
        assertNotNull(update.getMessage());
        assertEquals("Hello", update.getMessage().getText());
    }

    /**
     * Test case: T = BotInfo (another single-object generic, different from Update).
     * Ensures the pattern works consistently across all ZaloApiResponse subclasses.
     */
    @Test
    void getMeResponse_deserializesToBotInfo() throws Exception {
        String json = "{\"ok\":true,\"result\":{\"id\":\"12345\",\"account_name\":\"My Bot\",\"account_type\":\"official\",\"can_join_groups\":true}}";

        GetMeResponse response = mapper.readValue(json, GetMeResponse.class);

        assertTrue(response.isOk());
        assertNotNull(response.getResult());
        assertEquals("12345", response.getResult().getId());
        assertEquals("My Bot", response.getResult().getAccountName());
        assertEquals("official", response.getResult().getAccountType());
        assertTrue(response.getResult().isCanJoinGroups());
    }

    /**
     * Test case: error response (ok = false) with error_code and description.
     */
    @Test
    void errorResponse_deserializesErrorFields() throws Exception {
        String json = "{\"ok\":false,\"error_code\":400,\"description\":\"Bad Request: chat not found\"}";

        GetUpdatesResponse response = mapper.readValue(json, GetUpdatesResponse.class);

        assertFalse(response.isOk());
        assertEquals(400, response.getErrorCode());
        assertEquals("Bad Request: chat not found", response.getDescription());
        assertNull(response.getResult());
    }
}
