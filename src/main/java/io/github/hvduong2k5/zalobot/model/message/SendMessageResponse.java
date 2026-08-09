package io.github.hvduong2k5.zalobot.model.message;

import io.github.hvduong2k5.zalobot.model.base.BaseResponse;

public class SendMessageResponse extends BaseResponse {
    private SendMessageResult result;

    public SendMessageResult getResult() { return result; }
    public void setResult(SendMessageResult result) { this.result = result; }
}
