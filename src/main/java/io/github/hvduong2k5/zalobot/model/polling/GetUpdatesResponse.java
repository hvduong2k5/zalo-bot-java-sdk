package io.github.hvduong2k5.zalobot.model.polling;

import io.github.hvduong2k5.zalobot.model.base.ZaloApiResponse;
import io.github.hvduong2k5.zalobot.model.update.Update;

/**
 * Response from the {@code /getUpdates} endpoint.
 * <p>
 * Unlike Telegram, Zalo's {@code getUpdates} returns a <strong>single</strong> {@link Update}
 * object in the {@code result} field, not an array.
 */
public class GetUpdatesResponse extends ZaloApiResponse<Update> {
}
