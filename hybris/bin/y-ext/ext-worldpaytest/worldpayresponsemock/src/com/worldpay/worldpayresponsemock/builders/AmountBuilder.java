package com.worldpay.worldpayresponsemock.builders;

import com.worldpay.internal.model.Amount;

/**
 * Builder for the internal Amount model generated from the Worldpay DTD
 */
public final class AmountBuilder {

    private static final String DEBIT_CREDIT_INDICATOR = "debit";
    private String transactionAmount = "100";
    private String exponent = "2";
    private String currencyCode = "USD";

    private AmountBuilder() {
    }

    /**
     * Factory method to create a builder
     * @return an Amount builder object
     */
    public static AmountBuilder anAmountBuilder() {
        return new AmountBuilder();
    }

    /**
     * Build with this given value
     * @param transactionAmount
     * @return this builder
     */
    public AmountBuilder withAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    /**
     * Build with this given value
     * @param exponent
     * @return this builder
     */
    public AmountBuilder withExponent(String exponent) {
        this.exponent = exponent;
        return this;
    }

    /**
     * Build with this given value
     * @param currencyCode
     * @return this builder
     */
    public AmountBuilder withCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    /**
     * Build the Amount object based on the builders internal state
     * @return the internal Amount model
     */
    public Amount build() {
        final Amount amount = new Amount();
        amount.setValue(transactionAmount);
        amount.setCurrencyCode(currencyCode);
        amount.setExponent(String.valueOf(exponent));
        amount.setDebitCreditIndicator(DEBIT_CREDIT_INDICATOR);
        return amount;
    }
}
