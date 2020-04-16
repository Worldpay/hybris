package com.worldpay.service.model;

import java.io.Serializable;

public class SchemeResponse implements Serializable {

    private String schemeName;
    private String actionCode;
    private String responseCode;
    private String transactionIdentifier;

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(final String schemeName) {
        this.schemeName = schemeName;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(final String actionCode) {
        this.actionCode = actionCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(final String responseCode) {
        this.responseCode = responseCode;
    }

    public String getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public void setTransactionIdentifier(final String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }

    @Override
    public String toString() {
        return "SchemeResponse{" +
                "schemeName='" + schemeName + '\'' +
                ", actionCode='" + actionCode + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", transactionIdentifier='" + transactionIdentifier + '\'' +
                '}';
    }
}
