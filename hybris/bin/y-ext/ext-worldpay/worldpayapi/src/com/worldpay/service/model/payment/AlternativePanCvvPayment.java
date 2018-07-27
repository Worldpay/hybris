package com.worldpay.service.model.payment;

import com.worldpay.internal.model.ExpiryDate;
import com.worldpay.service.model.Date;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * POJO representation of {@link AlternativePayment} type with pan, cvv and expiry date
 *
 * @see PaymentBuilder PaymentBuilder for simple static creation methods
 */
public class AlternativePanCvvPayment extends AlternativePayment {

    private String pan;
    private String cvv;
    private Date expiryDate;

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
     * @param cvv
     * @param expiryDate
     * @see PaymentBuilder PaymentBuilder for simple static creation methods
     */
    public AlternativePanCvvPayment(final PaymentType paymentType, final String shopperCountryCode, final String successURL, final String failureURL, final String cancelURL, final String pendingURL, String pan, String cvv, Date expiryDate) {
        super(paymentType, shopperCountryCode, successURL, failureURL, cancelURL, pendingURL);
        this.pan = pan;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }

    @Override
    protected void invokeExtraSetters(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        super.invokeExtraSetters(method, targetObject);
        final String methodName = method.getName();
        if ("setPan".equals(methodName) && getPan() != null) {
            method.invoke(targetObject, getPan());
        }
        if ("setCvv".equals(methodName) && getCvv() != null) {
            method.invoke(targetObject, getCvv());
        }
        if ("setExpiryDate".equals(methodName) && getExpiryDate() != null) {
            final ExpiryDate intExpiryDate = new ExpiryDate();
            intExpiryDate.setDate((com.worldpay.internal.model.Date) getExpiryDate().transformToInternalModel());
            method.invoke(targetObject, intExpiryDate);
        }
    }

    public String getPan() {
        return pan;
    }

    public void setPan(final String pan) {
        this.pan = pan;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(final String cvv) {
        this.cvv = cvv;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "AlternativePanCvvPayment [pan=" + pan + ", cvv=" + cvv + ", expiryDate=" + expiryDate + "]";
    }
}
