package com.worldpay.service.model.payment;

import com.worldpay.internal.model.CardAddress;
import com.worldpay.service.model.Address;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * POJO representation of {@link AlternativePayment} type with card address
 *
 * @see PaymentBuilder PaymentBuilder for simple static creation methods
 */
public class AlternativeCardAddressPayment extends AlternativePayment {

    private Address cardAddress;

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
    public AlternativeCardAddressPayment(final PaymentType paymentType, final String shopperCountryCode, final String successURL, final String failureURL, final String cancelURL, String pendingURL, Address cardAddress) {
        super(paymentType, shopperCountryCode, successURL, failureURL, cancelURL, pendingURL);
        this.cardAddress = cardAddress;
    }

    @Override
    protected void invokeExtraSetters(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        super.invokeExtraSetters(method, targetObject);
        final String methodName = method.getName();
        if ("setCardAddress".equals(methodName) && cardAddress != null) {
            final CardAddress intCardAddress = new CardAddress();
            intCardAddress.setAddress((com.worldpay.internal.model.Address) cardAddress.transformToInternalModel());
            method.invoke(targetObject, intCardAddress);
        }
    }

    public Address getCardAddress() {
        return cardAddress;
    }

    public void setCardAddress(final Address cardAddress) {
        this.cardAddress = cardAddress;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "AlternativeCardAddressPayment [cardAddress=" + cardAddress + "]";
    }
}
