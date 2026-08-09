package io.github.hvduong2k5.zalobot.model.webhook;

import io.github.hvduong2k5.zalobot.model.base.BaseResponse;

import io.github.hvduong2k5.zalobot.model.webhook.Update;
import java.util.List;

public class GetUpdatesResponse extends BaseResponse {
    private List<Update> result;

    public List<Update> getResult() { return result; }
    public void setResult(List<Update> result) { this.result = result; }
}
