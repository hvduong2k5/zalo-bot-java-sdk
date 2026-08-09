package io.github.hvduong2k5.zalobot.model.response;

public class GetMeResponse extends BaseResponse {
    private BotInfo result;

    public BotInfo getResult() { return result; }
    public void setResult(BotInfo result) { this.result = result; }
}
