package io.github.hvduong2k5.zalobot;

import io.github.hvduong2k5.zalobot.model.bot.BotInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ZaloBotClientIT {

    private static ZaloBotClient client;
    private static String testUserId;

    @BeforeAll
    static void setUp() {
        String token = System.getenv("ZALO_BOT_TOKEN");
        testUserId = System.getenv("TEST_USER_ID");

        // Bỏ qua toàn bộ test trong class này nếu không có token (ví dụ: chạy ở local dev)
        assumeTrue(token != null && !token.trim().isEmpty(), "ZALO_BOT_TOKEN is missing. Skipping Integration Tests.");

        client = ZaloBotClient.builder()
                .botToken(token)
                .build();
    }

    @Test
    void testGetMe_WithRealToken_ShouldReturnBotInfo() {
        BotInfo botInfo = client.getMe();
        
        assertNotNull(botInfo);
        assertNotNull(botInfo.getId(), "Bot ID should not be null");
        assertNotNull(botInfo.getAccountName(), "Bot Account Name should not be null");
    }

    // Các test khác có thể thêm ở đây, ví dụ test gửi tin nhắn nếu TEST_USER_ID được cung cấp:
    // @Test
    // void testSendMessage() {
    //     assumeTrue(testUserId != null && !testUserId.trim().isEmpty(), "TEST_USER_ID is missing.");
    //     // ... code gửi tin nhắn ...
    // }
}
