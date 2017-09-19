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
    public AlternativePanPinPayment(PaymentType paymentType, String shopperCountryCode, String successURL, String failureURL, String cancelURL, String pendingURL, String pan, String pin) {
        super(paymentType, shopperCountryCode, successURL, failureURL, cancelURL, pendingURL);
        this.pan = pan;
        this.pin = pin;
    }

    @Override
    protected void invokeExtraSetters(Method method, Object targetObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        super.invokeExtraSetters(method, targetObject);
        String methodName = method.getName();
        if (methodName.equals("setPan") && getPan() != null) {
            method.invoke(targetObject, getPan());
        }
        if (methodName.equals("setPin") && getPin() != null) {
            method.invoke(targetObject, getPin());
        }
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
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
