package com.worldpay.service.model.payment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * POJO representation of {@link AlternativePayment} type with shopper bank code
 *
 * @see PaymentBuilder PaymentBuilder for simple static creation methods
 */
public class AlternativeShopperBankCodePayment extends AlternativePayment {

    private String shopperBankCode;

    /**
     * Constructor with full list of fields
     *
     * @param paymentType
     * @param shopperCountryCode
     * @param successURL
     * @param failureURL
     * @param cancelURL
     * @param pendingURL
     * @param shopperBankCode
     * @see PaymentBuilder PaymentBuilder for simple static creation methods
     */
    public AlternativeShopperBankCodePayment(final PaymentType paymentType, final String shopperCountryCode, final String successURL, final String failureURL, final String cancelURL, final String pendingURL, final String shopperBankCode) {
        super(paymentType, shopperCountryCode, successURL, failureURL, cancelURL, pendingURL);
        this.shopperBankCode = shopperBankCode;
    }

    @Override
    protected void invokeExtraSetters(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        super.invokeExtraSetters(method, targetObject);
        final String methodName = method.getName();
        if ("setShopperBankCode".equals(methodName) && shopperBankCode != null) {
            method.invoke(targetObject, shopperBankCode);
        }
    }

    public String getShopperBankCode() {
        return shopperBankCode;
    }

    public void setShopperBankCode(final String shopperBankCode) {
        this.shopperBankCode = shopperBankCode;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "AlternativeShopperBankCodePayment [shopperBankCode=" + shopperBankCode + "]";
    }
}
