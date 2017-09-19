package com.worldpay.service.model.payment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * POJO representation of {@link AlternativePayment} type with paypal first in billing run parameter
 *
 * @see PaymentBuilder PaymentBuilder for simple static creation methods
 */
public class AlternativePayPalPayment extends AlternativePayment {

    private String firstInBillingRun;

    /**
     * Constructor with full list of fields
     *
     * @param paymentType
     * @param shopperCountryCode
     * @param successURL
     * @param failureURL
     * @param cancelURL
     * @param pendingURL
     * @param firstInBillingRun
     * @see PaymentBuilder PaymentBuilder for simple static creation methods
     */
    public AlternativePayPalPayment(PaymentType paymentType, String shopperCountryCode, String successURL, String failureURL, String cancelURL, String pendingURL, String firstInBillingRun) {
        super(paymentType, shopperCountryCode, successURL, failureURL, cancelURL, pendingURL);
        this.firstInBillingRun = firstInBillingRun;
    }

    @Override
    protected void invokeExtraSetters(Method method, Object targetObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        super.invokeExtraSetters(method, targetObject);
        String methodName = method.getName();
        if (methodName.equals("setFirstInBillingRun") && firstInBillingRun != null) {
            method.invoke(targetObject, firstInBillingRun);
        }
    }

    public String getFirstInBillingRun() {
        return firstInBillingRun;
    }

    public void setFirstInBillingRun(String firstInBillingRun) {
        this.firstInBillingRun = firstInBillingRun;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "AlternativePayPalPayment [firstInBillingRun=" + firstInBillingRun + "]";
    }
}
