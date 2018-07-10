package com.worldpay.service.model.token;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.internal.model.PaymentInstrument;
import com.worldpay.internal.model.PaymentTokenID;
import com.worldpay.internal.model.TOKENSSL;
import com.worldpay.service.model.payment.AbstractPayment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

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
    public InternalModelObject transformToInternalModel() throws WorldpayModelTransformationException {
        final TOKENSSL tokenssl = new TOKENSSL();
        tokenssl.setTokenScope(merchantToken ? "merchant" : "shopper");

        final List<Object> tokenElements = tokenssl.getPaymentTokenIDOrPaymentInstrumentOrCvcOrSession();
        if (paymentTokenID != null) {
            final PaymentTokenID paymentTokenIDElement = new PaymentTokenID();
            paymentTokenIDElement.setvalue(paymentTokenID);
            tokenElements.add(paymentTokenIDElement);
        }
        if (paymentInstrument != null) {
            final PaymentInstrument intPaymentInstrument = new PaymentInstrument();
            final com.worldpay.internal.model.CardDetails intCardDetails = (com.worldpay.internal.model.CardDetails) paymentInstrument.transformToInternalModel();
            intPaymentInstrument.getCardDetailsOrPaypalOrSepaOrEmvcoTokenDetails().add(intCardDetails);
            tokenElements.add(intPaymentInstrument);
        }

        return tokenssl;
    }

    @Override
    public void invokeSetter(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        // DO nothing
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
