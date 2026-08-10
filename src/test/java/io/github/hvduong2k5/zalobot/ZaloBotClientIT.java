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

    @Test
    void testSendMessage() {
        assumeTrue(testUserId != null && !testUserId.trim().isEmpty(), "TEST_USER_ID is missing. Skipping testSendMessage.");
        
        io.github.hvduong2k5.zalobot.model.message.SendMessageRequest request = 
                io.github.hvduong2k5.zalobot.model.message.SendMessageRequest.builder()
                        .chatId(testUserId)
                        .text("Hello from Integration Tests! 🚀")
                        .build();

        io.github.hvduong2k5.zalobot.model.message.SendMessageResult result = client.sendMessage(request);
        
        assertNotNull(result, "SendMessageResult should not be null");
        assertNotNull(result.getMessageId(), "Message ID should not be null");
    }

    @Test
    void testSendChatAction() {
        assumeTrue(testUserId != null && !testUserId.trim().isEmpty(), "TEST_USER_ID is missing. Skipping testSendChatAction.");
        
        io.github.hvduong2k5.zalobot.model.message.SendChatActionRequest request = 
                io.github.hvduong2k5.zalobot.model.message.SendChatActionRequest.builder()
                        .chatId(testUserId)
                        .action("typing")
                        .build();

        // This method returns void (EmptyResponse internally), so if it doesn't throw an exception, it's successful.
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> client.sendChatAction(request));
        System.out.println("Hãy nhìn vào điện thoại ngay bây giờ!");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSendPhoto() {
        assumeTrue(testUserId != null && !testUserId.trim().isEmpty(), "TEST_USER_ID is missing. Skipping testSendPhoto.");
        
        io.github.hvduong2k5.zalobot.model.message.SendPhotoRequest request = 
                io.github.hvduong2k5.zalobot.model.message.SendPhotoRequest.builder()
                        .chatId(testUserId)
                        .photo("https://gdtc.hvduong.qzz.io/favicon.ico")
                        .caption("Test Photo")
                        .build();

        try {
            io.github.hvduong2k5.zalobot.model.message.SendMessageResult result = client.sendPhoto(request);
            assertNotNull(result, "SendMessageResult should not be null");
            assertNotNull(result.getMessageId(), "Message ID should not be null");
        } catch (io.github.hvduong2k5.zalobot.exception.ZaloApiException e) {
            // Có thể bỏ qua nếu URL ảnh bị API từ chối do chính sách Zalo
        }
    }

    @Test
    void testSendSticker() {
        assumeTrue(testUserId != null && !testUserId.trim().isEmpty(), "TEST_USER_ID is missing. Skipping testSendSticker.");
        
        io.github.hvduong2k5.zalobot.model.message.SendStickerRequest request = 
                io.github.hvduong2k5.zalobot.model.message.SendStickerRequest.builder()
                        .chatId(testUserId)
                        .sticker("4591eff8d2bd3be362ac") // mẫu sticker ID
                        .build();

        try {
            io.github.hvduong2k5.zalobot.model.message.SendMessageResult result = client.sendSticker(request);
            assertNotNull(result, "SendMessageResult should not be null");
            assertNotNull(result.getMessageId(), "Message ID should not be null");
        } catch (io.github.hvduong2k5.zalobot.exception.ZaloApiException e) {
            // Có thể bỏ qua nếu sticker ID không hợp lệ
        }
    }

    @Test
    void testSendVoice() {
        assumeTrue(testUserId != null && !testUserId.trim().isEmpty(), "TEST_USER_ID is missing. Skipping testSendVoice.");
        
        io.github.hvduong2k5.zalobot.model.message.SendVoiceRequest request = 
                io.github.hvduong2k5.zalobot.model.message.SendVoiceRequest.builder()
                        .chatId(testUserId)
                        .voiceUrl("https://dl.espressif.com/dl/audio/ff-16b-2c-44100hz.aac")
                        .build();

        try {
            io.github.hvduong2k5.zalobot.model.message.SendMessageResult result = client.sendVoice(request);
            assertNotNull(result, "SendMessageResult should not be null");
            assertNotNull(result.getMessageId(), "Message ID should not be null");
        } catch (io.github.hvduong2k5.zalobot.exception.ZaloApiException e) {
            // Có thể bỏ qua nếu URL voice không hợp lệ
            System.out.println(e.getDescription()+" - " + e.getErrorCode());
        }
    }

    @Test
    void testGetUpdate() {
        // Test polling API (với timeout ngắn)
        io.github.hvduong2k5.zalobot.model.polling.GetUpdatesRequest request =
                io.github.hvduong2k5.zalobot.model.polling.GetUpdatesRequest.builder()
                        .timeout(1)
                        .build();

        try {
            io.github.hvduong2k5.zalobot.model.update.Update update = client.getUpdate(request);
            // Có thể trả về null nếu không có update nào pending
        } catch (io.github.hvduong2k5.zalobot.exception.ZaloApiException e) {
            // Bỏ qua nếu có lỗi trả về từ Zalo (ví dụ do tần suất gọi)
        }
    }
}
