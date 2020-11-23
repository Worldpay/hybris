package com.worldpay.service.model;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodAttributeTest {

    private static final String ATTR_NAME = "attrName";
    private static final String ATTR_VALUE = "attrValue";
    private static final String PAYMENT_METHOD = "paymentMethod";

    @Test
    public void populate_shouldSettAttrNameAttrValueAndPaymentMethodName() {
        final com.worldpay.service.model.PaymentMethodAttribute paymentMethodAttribute = new com.worldpay.service.model.PaymentMethodAttribute();
        paymentMethodAttribute.setAttrName(ATTR_NAME);
        paymentMethodAttribute.setAttrValue(ATTR_VALUE);
        paymentMethodAttribute.setPaymentMethod(PAYMENT_METHOD);

        final com.worldpay.internal.model.PaymentMethodAttribute intPaymentMethodAttribute = (com.worldpay.internal.model.PaymentMethodAttribute) paymentMethodAttribute.transformToInternalModel();

        assertThat(intPaymentMethodAttribute.getAttrName()).isEqualTo(ATTR_NAME);
        assertThat(intPaymentMethodAttribute.getAttrValue()).isEqualTo(ATTR_VALUE);
        assertThat(intPaymentMethodAttribute.getPaymentMethod()).isEqualTo(PAYMENT_METHOD);
    }
}

