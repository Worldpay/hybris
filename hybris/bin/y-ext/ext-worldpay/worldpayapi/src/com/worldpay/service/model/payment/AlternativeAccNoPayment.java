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
    public AlternativeAccNoPayment(PaymentType paymentType, String shopperCountryCode, String successURL, String failureURL, String cancelURL, String pendingURL, String accountNumber, String accountPassword) {
        super(paymentType, shopperCountryCode, successURL, failureURL, cancelURL, pendingURL);
        this.accountNumber = accountNumber;
        this.accountPassword = accountPassword;
    }

    @Override
    protected void invokeExtraSetters(Method method, Object targetObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        super.invokeExtraSetters(method, targetObject);
        String methodName = method.getName();
        if (methodName.equals("setAccountNumber") && getAccountNumber() != null) {
            method.invoke(targetObject, getAccountNumber());
        }
        if (methodName.equals("setAccountPassword") && getAccountPassword() != null) {
            method.invoke(targetObject, getAccountPassword());
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
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
