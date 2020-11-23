package com.worldpay.service.model.payment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * POJO representation of {@link Payment} type for envoy transfer payments
 *
 * @see PaymentBuilder PaymentBuilder for simple static creation methods
 */
public class EnvoyTransferPayment extends AbstractPayment {

    private String shopperCountryCode;

    /**
     * Constructor with full list of fields
     *
     * @param paymentType
     * @param shopperCountryCode
     * @see PaymentBuilder PaymentBuilder for simple static creation methods
     */
    public EnvoyTransferPayment(final PaymentType paymentType, final String shopperCountryCode) {
        this.setPaymentType(paymentType);
        this.shopperCountryCode = shopperCountryCode;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.worldpay.service.model.payment.AbstractPayment#invokeSetter(Method, Object)
     */
    @Override
    public void invokeSetter(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        boolean methodInvoked = false;
        final String methodName = method.getName();
        if (methodName.startsWith("set") && "setShopperCountryCode".equals(methodName) && shopperCountryCode != null) {
            method.invoke(targetObject, shopperCountryCode);
            methodInvoked = true;
        }

        if (!methodInvoked) {
            invokeExtraSetters(method, targetObject);
        }
    }

    /**
     * Method to be used by overriding classes in order to ensure that extra fields that they implement get set when the {@link #transformToInternalModel()} method is
     * invoked. Default implementation does nothing so just provides the hook for overriding classes
     *
     * @param method       Method that can be invoked on the internal model object targetObject
     * @param targetObject internal model object that we are trying to transform to
     * @throws IllegalArgumentException  if the method is invoked with incorrect parameters
     * @throws IllegalAccessException    if the method is not accessible
     * @throws InvocationTargetException if method cannot be invoked against the supplied target object
     */
    protected void invokeExtraSetters(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        // Do nothing. This provides a hook for subclasses to add extra functionality
    }

    public String getShopperCountryCode() {
        return shopperCountryCode;
    }

    public void setShopperCountryCode(final String shopperCountryCode) {
        this.shopperCountryCode = shopperCountryCode;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "EnvoyTransferPayment [shopperCountryCode=" + shopperCountryCode + "]";
    }
}
