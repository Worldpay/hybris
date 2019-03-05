package com.worldpay.service.model.payment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PayWithGoogleSSL extends AbstractPayment {
    private String protocolVersion;
    private String signature;
    private String signedMessage;

    public PayWithGoogleSSL(final String protocolVersion, final String signature, final String signedMessage) {
        this.protocolVersion = protocolVersion;
        this.signature = signature;
        this.signedMessage = signedMessage;
        this.setPaymentType(PaymentType.PAYWITHGOOGLESSL);
    }

    @Override
    public void invokeSetter(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException {
        final String methodName = method.getName();
        if ("setProtocolVersion".equals(methodName) && protocolVersion != null) {
            method.invoke(targetObject, protocolVersion);
        }
        if ("setSignature".equals(methodName) && signature != null) {
            method.invoke(targetObject, signature);
        }
        if ("setSignedMessage".equals(methodName) && signedMessage != null) {
            method.invoke(targetObject, signedMessage);
        }
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(final String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(final String signature) {
        this.signature = signature;
    }

    public String getSignedMessage() {
        return signedMessage;
    }

    public void setSignedMessage(final String signedMessage) {
        this.signedMessage = signedMessage;
    }
}
