package com.worldpay.service.model.payment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * POJO representation of {@link Payment} type for alternative payments
 *
 * @see PaymentBuilder PaymentBuilder for simple static creation methods
 */
public class AlternativePayment extends AbstractPayment {

    private String shopperCountryCode;
    private String successURL;
    private String failureURL;
    private String cancelURL;
    private String pendingURL;

    /**
     * Constructor with full list of fields
     *
     * @param paymentType
     * @param shopperCountryCode
     * @param successURL
     * @param failureURL
     * @param cancelURL
     * @param pendingURL
     * @see PaymentBuilder PaymentBuilder for simple static creation methods
     */
    public AlternativePayment(final PaymentType paymentType, final String shopperCountryCode, final String successURL, final String failureURL, final String cancelURL, final String pendingURL) {
        this.paymentType = paymentType;
        this.shopperCountryCode = shopperCountryCode;
        this.successURL = successURL;
        this.failureURL = failureURL;
        this.cancelURL = cancelURL;
        this.pendingURL = pendingURL;
    }

    @Override
    public void invokeSetter(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        boolean methodInvoked = false;
        final String methodName = method.getName();
        if (methodName.startsWith("set")) {
            if ("setShopperCountryCode".equals(methodName) && shopperCountryCode != null) {
                method.invoke(targetObject, shopperCountryCode);
                methodInvoked = true;
            }
            if ("setSuccessURL".equals(methodName) && successURL != null) {
                method.invoke(targetObject, successURL);
                methodInvoked = true;
            }
            if ("setFailureURL".equals(methodName) && failureURL != null) {
                method.invoke(targetObject, failureURL);
                methodInvoked = true;
            }
            if ("setCancelURL".equals(methodName) && cancelURL != null) {
                method.invoke(targetObject, cancelURL);
                methodInvoked = true;
            }
            if ("setPendingURL".equals(methodName) && pendingURL != null) {
                method.invoke(targetObject, pendingURL);
                methodInvoked = true;
            }
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

    public String getSuccessURL() {
        return successURL;
    }

    public void setSuccessURL(final String successURL) {
        this.successURL = successURL;
    }

    public String getFailureURL() {
        return failureURL;
    }

    public void setFailureURL(final String failureURL) {
        this.failureURL = failureURL;
    }

    public String getCancelURL() {
        return cancelURL;
    }

    public void setCancelURL(final String cancelURL) {
        this.cancelURL = cancelURL;
    }

    public String getPendingURL() {
        return pendingURL;
    }

    public void setPendingURL(final String pendingURL) {
        this.pendingURL = pendingURL;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "AlternativePayment [shopperCountryCode=" + shopperCountryCode + ", successURL=" + successURL + ", failureURL=" + failureURL +
                ", cancelURL=" + cancelURL + ", pendingURL=" + pendingURL + "]";
    }
}
