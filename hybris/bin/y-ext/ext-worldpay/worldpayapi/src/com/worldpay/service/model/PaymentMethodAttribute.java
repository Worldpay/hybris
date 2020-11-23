
package com.worldpay.service.model;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;

public class PaymentMethodAttribute implements InternalModelTransformer, Serializable {

    private String paymentMethod;

    private String attrName;

    private String attrValue;

    @Override
    public InternalModelObject transformToInternalModel() {
        final com.worldpay.internal.model.PaymentMethodAttribute intPaymentMethodAttribute = new com.worldpay.internal.model.PaymentMethodAttribute();
        intPaymentMethodAttribute.setAttrName(this.attrName);
        intPaymentMethodAttribute.setAttrValue(this.attrValue);
        intPaymentMethodAttribute.setPaymentMethod(this.paymentMethod);
        return intPaymentMethodAttribute;
    }


    public void setPaymentMethod(final String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setAttrName(final String attrName) {
        this.attrName = attrName;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrValue(final String attrValue) {
        this.attrValue = attrValue;
    }

    public String getAttrValue() {
        return attrValue;
    }

    @Override
    public String toString() {
        return "PaymentMethodAttribute{" +
            "paymentMethod='" + paymentMethod + '\'' +
            ", attrName='" + attrName + '\'' +
            ", attrValue='" + attrValue + '\'' +
            '}';
    }
}
