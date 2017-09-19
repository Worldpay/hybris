package com.worldpay.service.model.token;

import com.worldpay.service.model.Date;

import java.io.Serializable;

public class TokenDetails implements Serializable {

    private String tokenEvent;
    private String paymentTokenID;
    private String reportingTokenID;
    private String tokenReason;
    private String tokenEventReference;
    private Date paymentTokenExpiry;
    private Date reportingTokenExpiry;

    public String getTokenEvent() {
        return tokenEvent;
    }

    public void setTokenEvent(final String tokenEvent) {
        this.tokenEvent = tokenEvent;
    }

    public String getPaymentTokenID() {
        return paymentTokenID;
    }

    public void setPaymentTokenID(final String paymentTokenID) {
        this.paymentTokenID = paymentTokenID;
    }

    public String getReportingTokenID() {
        return reportingTokenID;
    }

    public void setReportingTokenID(final String reportingTokenID) {
        this.reportingTokenID = reportingTokenID;
    }

    public String getTokenReason() {
        return tokenReason;
    }

    public void setTokenReason(final String tokenReason) {
        this.tokenReason = tokenReason;
    }

    public String getTokenEventReference() {
        return tokenEventReference;
    }

    public void setTokenEventReference(final String tokenEventReference) {
        this.tokenEventReference = tokenEventReference;
    }

    public Date getPaymentTokenExpiry() {
        return paymentTokenExpiry;
    }

    public void setPaymentTokenExpiry(final Date paymentTokenExpiry) {
        this.paymentTokenExpiry = paymentTokenExpiry;
    }

    public Date getReportingTokenExpiry() {
        return reportingTokenExpiry;
    }

    public void setReportingTokenExpiry(final Date reportingTokenExpiry) {
        this.reportingTokenExpiry = reportingTokenExpiry;
    }

    @Override
    public String toString() {
        return "TokenDetails{" +
                "tokenEvent='" + tokenEvent + '\'' +
                ", paymentTokenID='" + paymentTokenID + '\'' +
                ", reportingTokenID='" + reportingTokenID + '\'' +
                ", tokenReason='" + tokenReason + '\'' +
                ", tokenEventReference='" + tokenEventReference + '\'' +
                ", paymentTokenExpiry=" + paymentTokenExpiry +
                ", reportingTokenExpiry=" + reportingTokenExpiry +
                '}';
    }
}
