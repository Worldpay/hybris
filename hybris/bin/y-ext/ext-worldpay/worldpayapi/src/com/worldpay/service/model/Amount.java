package com.worldpay.service.model;


import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

/**
 * POJO representation of an amount
 */
public class Amount implements InternalModelTransformer, Serializable {

    private String value;
    private String currencyCode;
    private String exponent;
    private DebitCreditIndicator debitCreditIndicator;

    /**
     * Constructor assumes that the debit credit indicator is CREDIT
     *
     * @param value
     * @param currencyCode
     * @param exponent
     */
    public Amount(String value, String currencyCode, String exponent) {
        this.value = value;
        this.currencyCode = currencyCode;
        this.exponent = exponent;
        this.debitCreditIndicator = DebitCreditIndicator.CREDIT;
    }

    /**
     * Constructor with full list of fields
     *
     * @param value
     * @param currencyCode
     * @param exponent
     * @param debitCreditIndicator
     */
    public Amount(String value, String currencyCode, String exponent, DebitCreditIndicator debitCreditIndicator) {
        this.value = value;
        this.currencyCode = currencyCode;
        this.exponent = exponent;
        this.debitCreditIndicator = debitCreditIndicator;
    }

    @Override
    public com.worldpay.internal.model.Amount transformToInternalModel() {
        final com.worldpay.internal.model.Amount intAmount = new com.worldpay.internal.model.Amount();
        intAmount.setValue(value);
        intAmount.setCurrencyCode(currencyCode);
        intAmount.setExponent(exponent);

        if (debitCreditIndicator != null) {
            intAmount.setDebitCreditIndicator(debitCreditIndicator.getCode());
        }
        return intAmount;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getExponent() {
        return exponent;
    }

    public void setExponent(String exponent) {
        this.exponent = exponent;
    }

    public DebitCreditIndicator getDebitCreditIndicator() {
        return debitCreditIndicator;
    }

    public void setDebitCreditIndicator(DebitCreditIndicator debitCreditIndicator) {
        this.debitCreditIndicator = debitCreditIndicator;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "Amount [value=" + value + ", currencyCode=" + currencyCode + ", exponent=" + exponent + ", debitCreditIndicator=" + debitCreditIndicator + "]";
    }
}
