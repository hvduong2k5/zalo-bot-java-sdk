package io.github.hvduong2k5.zalobot.model.response;

public class SendMessageResponse extends BaseResponse {
    private SendMessageResult result;

    public SendMessageResult getResult() { return result; }
    public void setResult(SendMessageResult result) { this.result = result; }
}
