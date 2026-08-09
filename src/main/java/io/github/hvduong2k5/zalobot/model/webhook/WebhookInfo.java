package io.github.hvduong2k5.zalobot.model.webhook;

public class WebhookInfo {
    private String url;
    private boolean hasCustomCertificate;
    private int pendingUpdateCount;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public boolean isHasCustomCertificate() { return hasCustomCertificate; }
    public void setHasCustomCertificate(boolean hasCustomCertificate) { this.hasCustomCertificate = hasCustomCertificate; }

    public int getPendingUpdateCount() { return pendingUpdateCount; }
    public void setPendingUpdateCount(int pendingUpdateCount) { this.pendingUpdateCount = pendingUpdateCount; }
}
