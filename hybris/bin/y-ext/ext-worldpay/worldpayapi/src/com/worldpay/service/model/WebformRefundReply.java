package com.worldpay.service.model;

import java.io.Serializable;


public class WebformRefundReply implements Serializable {
    private String webformId;
    private String paymentId;
    private String webformStatus;
    private Amount amount;
    private String reason;
    private String webformURL;
    private String refundId;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getWebformStatus() {
        return webformStatus;
    }

    public void setWebformStatus(String webformStatus) {
        this.webformStatus = webformStatus;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getWebformURL() {
        return webformURL;
    }

    public void setWebformURL(String webformURL) {
        this.webformURL = webformURL;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }


    public String getWebformId() {
        return webformId;
    }

    public void setWebformId(String webformId) {
        this.webformId = webformId;
    }
}
