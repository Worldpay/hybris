package com.worldpay.service.model.token;

import com.worldpay.internal.model.PaymentInstrument;
import com.worldpay.service.model.payment.AbstractPayment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.worldpay.service.model.payment.PaymentType.TOKENSSL;

public class Token extends AbstractPayment {

    private String paymentTokenID;
    private CardDetails paymentInstrument;

    public Token(String paymentTokenID) {
        this.paymentTokenID = paymentTokenID;
        this.setPaymentType(TOKENSSL);
    }

    public Token(String paymentTokenID, final CardDetails paymentInstrument) {
        this.paymentTokenID = paymentTokenID;
        this.paymentInstrument = paymentInstrument;
        this.setPaymentType(TOKENSSL);
    }

    @Override
    public void invokeSetter(final Method method, final Object targetObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final String methodName = method.getName();
        if (methodName.equals("setPaymentTokenID") && paymentTokenID != null) {
            method.invoke(targetObject, paymentTokenID);
        }
        if (methodName.equals("setPaymentInstrument") && paymentInstrument != null) {
            final PaymentInstrument intPaymentInstrument = new PaymentInstrument();
            final com.worldpay.internal.model.CardDetails intCardDetails = (com.worldpay.internal.model.CardDetails) paymentInstrument.transformToInternalModel();
            intPaymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetails().add(intCardDetails);
            method.invoke(targetObject, intPaymentInstrument);
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
}
