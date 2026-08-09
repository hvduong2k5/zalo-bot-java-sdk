package io.github.hvduong2k5.zalobot.model.webhook;

import io.github.hvduong2k5.zalobot.model.base.BaseResponse;

public class WebhookInfoResponse extends BaseResponse {
    private WebhookInfo result;

    public WebhookInfo getResult() { return result; }
    public void setResult(WebhookInfo result) { this.result = result; }
}
