package io.github.hvduong2k5.zalobot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZaloBotClientBuilderTest {

    @Test
    void build_withoutToken_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ZaloBotClient.builder().build();
        });
        assertTrue(exception.getMessage().contains("botToken is required"));
    }

    @Test
    void build_withNullToken_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ZaloBotClient.builder().botToken(null).build();
        });
        assertTrue(exception.getMessage().contains("botToken is required"));
    }

    @Test
    void build_withEmptyToken_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ZaloBotClient.builder().botToken("   ").build();
        });
        assertTrue(exception.getMessage().contains("botToken is required"));
    }

    @Test
    void build_withValidToken_succeeds() {
        ZaloBotClient client = ZaloBotClient.builder().botToken("valid-token").build();
        assertNotNull(client);
    }
}
