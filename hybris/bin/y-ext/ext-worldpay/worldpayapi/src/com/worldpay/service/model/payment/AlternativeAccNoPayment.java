package com.worldpay.service.model.payment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * POJO representation of {@link AlternativePayment} type with account number and password
 *
 * @see PaymentBuilder PaymentBuilder for simple static creation methods
 */
public class AlternativeAccNoPayment extends AlternativePayment {

    private String accountNumber;
    private String accountPassword;

    /**
     * Constructor with full list of fields
     *
     * @param paymentType
     * @param shopperCountryCode
     * @param successURL
     * @param failureURL
     * @param cancelURL
     * @param pendingURL
     * @param accountNumber
     * @param accountPassword
     * @see PaymentBuilder PaymentBuilder for simple static creation methods
     */
    public AlternativeAccNoPayment(final PaymentType paymentType, final String shopperCountryCode, final String successURL, final String failureURL, final String cancelURL, final String pendingURL, final String accountNumber, final String accountPassword) {
        super(paymentType, shopperCountryCode, successURL, failureURL, cancelURL, pendingURL);
        this.accountNumber = accountNumber;
        this.accountPassword = accountPassword;
    }

    @Override
    protected void invokeExtraSetters(final Method method, final Object targetObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        super.invokeExtraSetters(method, targetObject);
        final String methodName = method.getName();
        if ("setAccountNumber".equals(methodName) && getAccountNumber() != null) {
            method.invoke(targetObject, getAccountNumber());
        }
        if ("setAccountPassword".equals(methodName) && getAccountPassword() != null) {
            method.invoke(targetObject, getAccountPassword());
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(final String accountPassword) {
        this.accountPassword = accountPassword;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "AlternativeAccNoPayment [accountNumber=" + accountNumber + ", accountPassword=" + accountPassword + "]";
    }
}
