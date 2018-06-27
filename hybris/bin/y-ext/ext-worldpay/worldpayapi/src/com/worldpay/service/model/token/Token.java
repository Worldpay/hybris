package com.worldpay.service.model.token;

import com.worldpay.internal.model.PaymentInstrument;
import com.worldpay.service.model.payment.AbstractPayment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.worldpay.service.model.payment.PaymentType.TOKENSSL;

public class Token extends AbstractPayment {

    private String paymentTokenID;
    private CardDetails paymentInstrument;
    private boolean merchantToken;

    public Token(final String paymentTokenID, final boolean merchantToken) {
        this.paymentTokenID = paymentTokenID;
        this.setPaymentType(TOKENSSL);
        this.merchantToken = merchantToken;
    }

    public Token(final String paymentTokenID, final CardDetails paymentInstrument, final boolean merchantToken) {
        this.paymentTokenID = paymentTokenID;
        this.paymentInstrument = paymentInstrument;
        this.merchantToken = merchantToken;
        this.setPaymentType(TOKENSSL);
    }

    @Override
    public void invokeSetter(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        final String methodName = method.getName();
        if ("setPaymentTokenID".equals(methodName) && paymentTokenID != null) {
            method.invoke(targetObject, paymentTokenID);
        }
        if ("setPaymentInstrument".equals(methodName) && paymentInstrument != null) {
            final PaymentInstrument intPaymentInstrument = new PaymentInstrument();
            final com.worldpay.internal.model.CardDetails intCardDetails = (com.worldpay.internal.model.CardDetails) paymentInstrument.transformToInternalModel();
            intPaymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetails().add(intCardDetails);
            method.invoke(targetObject, intPaymentInstrument);
        }
        if ("setTokenScope".equals(methodName)) {
            method.invoke(targetObject, merchantToken ? "merchant" : "shopper");
        }
    }

    public String getPaymentTokenID() {
        return paymentTokenID;
    }

    public void setPaymentTokenID(final String paymentTokenID) {
        this.paymentTokenID = paymentTokenID;
    }

    public CardDetails getPaymentInstrument() {
        return paymentInstrument;
    }

    public void setPaymentInstrument(CardDetails paymentInstrument) {
        this.paymentInstrument = paymentInstrument;
    }

    public boolean isMerchantToken() {
        return merchantToken;
    }

    public void setMerchantToken(final boolean merchantToken) {
        this.merchantToken = merchantToken;
    }
}
