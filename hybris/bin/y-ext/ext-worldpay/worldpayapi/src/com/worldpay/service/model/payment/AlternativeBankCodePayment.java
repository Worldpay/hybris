package com.worldpay.service.model.payment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * POJO representation of {@link AlternativePayment} type with bank code
 *
 * @see PaymentBuilder PaymentBuilder for simple static creation methods
 */
public class AlternativeBankCodePayment extends AlternativePayment {

    private String bankCode;

    /**
     * Constructor with full list of fields
     *
     * @param paymentType
     * @param shopperCountryCode
     * @param successURL
     * @param failureURL
     * @param cancelURL
     * @param pendingURL
     * @param bankCode
     * @see PaymentBuilder PaymentBuilder for simple static creation methods
     */
    public AlternativeBankCodePayment(final PaymentType paymentType, final String shopperCountryCode, final String successURL, final String failureURL, final String cancelURL, String pendingURL, String bankCode) {
        super(paymentType, shopperCountryCode, successURL, failureURL, cancelURL, pendingURL);
        this.bankCode = bankCode;
    }

    @Override
    protected void invokeExtraSetters(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        super.invokeExtraSetters(method, targetObject);
        final String methodName = method.getName();
        if ("setBankCode".equals(methodName) && bankCode != null) {
            method.invoke(targetObject, bankCode);
        }
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(final String bankCode) {
        this.bankCode = bankCode;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "AlternativeBankCodePayment [bankCode=" + bankCode + "]";
    }
}
