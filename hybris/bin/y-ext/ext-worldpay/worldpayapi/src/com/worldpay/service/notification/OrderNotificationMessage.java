package com.worldpay.service.notification;

import com.worldpay.data.JournalReply;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.WebformRefundReply;
import com.worldpay.data.token.TokenReply;

import java.io.Serializable;

/**
 * This class represents the details that are passed back from a notification message
 */
public class OrderNotificationMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderCode;
    private String merchantCode;
    private PaymentReply paymentReply;
    private JournalReply journalReply;
    private TokenReply tokenReply;
    private WebformRefundReply webformRefundReply;

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public PaymentReply getPaymentReply() {
        return paymentReply;
    }

    public void setPaymentReply(PaymentReply paymentReply) {
        this.paymentReply = paymentReply;
    }

    public JournalReply getJournalReply() {
        return journalReply;
    }

    public void setJournalReply(JournalReply journalReply) {
        this.journalReply = journalReply;
    }

    public TokenReply getTokenReply() {
        return tokenReply;
    }

    public void setTokenReply(TokenReply tokenReply) {
        this.tokenReply = tokenReply;
    }

    public WebformRefundReply getWebformRefundReply() {
        return webformRefundReply;
    }

    public void setWebformRefundReply(WebformRefundReply webformRefundReply) {
        this.webformRefundReply = webformRefundReply;
    }

    @Override
    public String toString() {
        return "OrderNotificationMessage{" +
                "orderCode='" + orderCode + '\'' +
                ", merchantCode='" + merchantCode + '\'' +
                ", paymentReply=" + paymentReply +
                ", journalReply=" + journalReply +
                ", tokenReply=" + tokenReply +
                ", webformRefundReply=" + webformRefundReply +
                '}';
    }
}
