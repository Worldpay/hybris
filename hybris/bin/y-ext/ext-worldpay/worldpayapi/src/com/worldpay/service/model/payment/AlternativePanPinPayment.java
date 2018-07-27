/**
 *
 */
package com.worldpay.service.model.payment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * POJO representation of {@link AlternativePayment} type with pan and pin
 *
 * @see PaymentBuilder PaymentBuilder for simple static creation methods
 */
public class AlternativePanPinPayment extends AlternativePayment {

    private String pan;
    private String pin;

    /**
     * Constructor with full list of fields
     *
     * @param paymentType
     * @param shopperCountryCode
     * @param successURL
     * @param failureURL
     * @param cancelURL
     * @param pendingURL
     * @param pan
     * @param pin
     * @see PaymentBuilder PaymentBuilder for simple static creation methods
     */
    public AlternativePanPinPayment(final PaymentType paymentType, final String shopperCountryCode, final String successURL, final String failureURL, final String cancelURL, final String pendingURL, final String pan, final String pin) {
        super(paymentType, shopperCountryCode, successURL, failureURL, cancelURL, pendingURL);
        this.pan = pan;
        this.pin = pin;
    }

    @Override
    protected void invokeExtraSetters(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        super.invokeExtraSetters(method, targetObject);
        final String methodName = method.getName();
        if ("setPan".equals(methodName) && getPan() != null) {
            method.invoke(targetObject, getPan());
        }
        if ("setPin".equals(methodName) && getPin() != null) {
            method.invoke(targetObject, getPin());
        }
    }

    public String getPan() {
        return pan;
    }

    public void setPan(final String pan) {
        this.pan = pan;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(final String pin) {
        this.pin = pin;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "AlternativePanPinPayment [pan=" + pan + ", pin=" + pin + "]";
    }
}
