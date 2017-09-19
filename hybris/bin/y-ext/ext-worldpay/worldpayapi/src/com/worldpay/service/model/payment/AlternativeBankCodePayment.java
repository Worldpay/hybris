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
    public AlternativeBankCodePayment(PaymentType paymentType, String shopperCountryCode, String successURL, String failureURL, String cancelURL, String pendingURL, String bankCode) {
        super(paymentType, shopperCountryCode, successURL, failureURL, cancelURL, pendingURL);
        this.bankCode = bankCode;
    }

    @Override
    protected void invokeExtraSetters(Method method, Object targetObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        super.invokeExtraSetters(method, targetObject);
        String methodName = method.getName();
        if (methodName.equals("setBankCode") && bankCode != null) {
            method.invoke(targetObject, bankCode);
        }
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
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
