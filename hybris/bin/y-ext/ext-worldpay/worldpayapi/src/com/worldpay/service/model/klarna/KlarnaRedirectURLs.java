package com.worldpay.service.model.klarna;

import java.io.Serializable;

public class KlarnaRedirectURLs implements Serializable {

    private String successURL;
    private String cancelURL;
    private String pendingURL;
    private String failureURL;

    public KlarnaRedirectURLs(String successURL, String cancelURL, String pendingURL, String failureURL) {
        this.successURL = successURL;
        this.cancelURL = cancelURL;
        this.pendingURL = pendingURL;
        this.failureURL = failureURL;
    }

    public String getSuccessURL() {
        return successURL;
    }

    public String getCancelURL() {
        return cancelURL;
    }

    public String getPendingURL() {
        return pendingURL;
    }

    public String getFailureURL() {
        return failureURL;
    }
}
