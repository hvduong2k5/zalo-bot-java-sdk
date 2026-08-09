package io.github.hvduong2k5.zalobot.model.bot;

import io.github.hvduong2k5.zalobot.model.base.BaseResponse;

public class GetMeResponse extends BaseResponse {
    private BotInfo result;

    public BotInfo getResult() { return result; }
    public void setResult(BotInfo result) { this.result = result; }
}
