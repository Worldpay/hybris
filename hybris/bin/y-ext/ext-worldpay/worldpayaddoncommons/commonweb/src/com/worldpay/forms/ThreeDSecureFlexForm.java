package com.worldpay.forms;

public class ThreeDSecureFlexForm {
    private String transactionId;
    private String md;
    private String response;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMd() {
        return md;
    }

    public void setMd(final String md) {
        this.md = md;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(final String response) {
        this.response = response;
    }
}
