package com.worldpay.worldpayresponsemock.builders;

import com.worldpay.internal.model.Amount;
import com.worldpay.internal.model.ShopperWebformRefundDetails;

/**
 * Builder for the internal ShopperWebformRefundDetails model generated from the Worldpay DTD
 */
public final class WebformRefundBuilder {

    private String webformId;
    private String paymentId;
    private String webformStatus;
    private String refundReason;
    private String webformURL;
    private String refundId;
    private String transactionAmount;
    private String currencyCode;
    private String exponent;

    private WebformRefundBuilder() {
    }

    /**
     * Factory method to create a builder
     * @return an ShopperWebformRefundDetails builder object
     */
    public static WebformRefundBuilder aWebformRefundBuilder() {
        return new WebformRefundBuilder();
    }

    /**
     * Build with this given value
     * @param webformId
     * @return this builder
     */
    public WebformRefundBuilder withWebformId(String webformId) {
        this.webformId = webformId;
        return this;
    }

    /**
     * Build with this given value
     * @param paymentId
     * @return this builder
     */
    public WebformRefundBuilder withPaymentId(String paymentId) {
        this.paymentId = paymentId;
        return this;
    }

    /**
     * Build with this given value
     * @param webformStatus
     * @return this builder
     */
    public WebformRefundBuilder withWebformStatus(String webformStatus) {
        this.webformStatus = webformStatus;
        return this;
    }

    /**
     * Build with this given value
     * @param refundReason
     * @return this builder
     */
    public WebformRefundBuilder withRefundReason(String refundReason) {
        this.refundReason = refundReason;
        return this;
    }

    /**
     * Build with this given value
     * @param webformURL
     * @return this builder
     */
    public WebformRefundBuilder withWebformURL(String webformURL) {
        this.webformURL = "<![CDATA[" + webformURL + "]]>";
        return this;
    }

    /**
     * Build with this given value
     * @param refundId
     * @return this builder
     */
    public WebformRefundBuilder withRefundId(String refundId) {
        this.refundId = "<![CDATA[" + refundId + "]]>";
        return this;
    }


    /**
     * Build with this given value
     * @param transactionAmount
     * @return this builder
     */
    public WebformRefundBuilder withTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    /**
     * Build with this given value
     * @param currencyCode
     * @return this builder
     */
    public WebformRefundBuilder withCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    /**
     * Build with this given value
     * @param exponent
     * @return this builder
     */
    public WebformRefundBuilder withExponent(String exponent) {
        this.exponent = exponent;
        return this;
    }

    /**
     * Build the ShopperWebformRefundDetails object based on the builders internal state
     * @return the internal ShopperWebformRefundDetails model
     */
    public ShopperWebformRefundDetails build() {
        final ShopperWebformRefundDetails shopperWebformRefundDetails = new ShopperWebformRefundDetails();

        final Amount amount = AmountBuilder.anAmountBuilder().
                withAmount(this.transactionAmount).
                withCurrencyCode(this.currencyCode).
                withExponent(this.exponent).
                build();

        shopperWebformRefundDetails.setAmount(amount);

        shopperWebformRefundDetails.setReason(this.refundReason);
        shopperWebformRefundDetails.setPaymentId(this.paymentId);
        shopperWebformRefundDetails.setWebformStatus(this.webformStatus);
        shopperWebformRefundDetails.setWebformURL(this.webformURL);
        shopperWebformRefundDetails.setWebformId(this.webformId);
        shopperWebformRefundDetails.setRefundId(this.refundId);

        return shopperWebformRefundDetails;
    }
}
