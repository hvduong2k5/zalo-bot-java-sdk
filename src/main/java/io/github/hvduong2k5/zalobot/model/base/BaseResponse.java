package io.github.hvduong2k5.zalobot.model.base;

public abstract class BaseResponse {
    private boolean ok;
    private Integer errorCode; // Integer, not int to avoid unboxing NPE
    private String description;

    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }

    public Integer getErrorCode() { return errorCode; }
    public void setErrorCode(Integer errorCode) { this.errorCode = errorCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
