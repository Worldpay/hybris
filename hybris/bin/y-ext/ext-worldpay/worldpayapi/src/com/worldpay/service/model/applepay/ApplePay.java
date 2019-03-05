package com.worldpay.service.model.applepay;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.service.model.payment.AbstractPayment;
import com.worldpay.service.model.payment.PaymentType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ApplePay extends AbstractPayment {

    private Header header;
    private String signature;
    private String version;
    private String data;
    private String tokenRequestorID;

    public ApplePay(final Header header, final String signature, final String version, final String data, final String tokenRequestorID) {
        this.setPaymentType(PaymentType.APPLEPAYSSL);
        this.header = header;
        this.signature = signature;
        this.version = version;
        this.data = data;
        this.tokenRequestorID = tokenRequestorID;
    }

    @Override
    public void invokeSetter(final Method method, final Object targetObject) throws IllegalAccessException, InvocationTargetException, WorldpayModelTransformationException {
        final String methodName = method.getName();

        if ("setHeader".equals(methodName) && header != null) {
            method.invoke(targetObject, header.transformToInternalModel());
        }
        if ("setSignature".equals(methodName) && signature != null) {
            method.invoke(targetObject, signature);
        }
        if ("setVersion".equals(methodName) && version != null) {
            method.invoke(targetObject, version);
        }
        if ("setData".equals(methodName) && data != null) {
            method.invoke(targetObject, data);
        }
        if ("setTokenRequestorID".equals(methodName) && tokenRequestorID != null) {
            method.invoke(targetObject, tokenRequestorID);
        }
    }


    public String getSignature() {
        return signature;
    }

    public void setSignature(final String signature) {
        this.signature = signature;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getData() {
        return data;
    }

    public void setData(final String data) {
        this.data = data;
    }

    public String getTokenRequestorID() {
        return tokenRequestorID;
    }

    public void setTokenRequestorID(final String tokenRequestorID) {
        this.tokenRequestorID = tokenRequestorID;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(final Header header) {
        this.header = header;
    }
}
