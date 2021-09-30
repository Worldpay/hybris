package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.PaymentMethodAttribute;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodAttributePopulatorTest {

    private static final String ATTR_NAME = "attrName";
    private static final String ATTR_VALUE = "attrValue";
    private static final String PAYMENT_METHOD = "paymentMethod";

    @InjectMocks
    private PaymentMethodAttributePopulator testObj;

    @Mock
    private PaymentMethodAttribute sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowException() {
        testObj.populate(null, new com.worldpay.internal.model.PaymentMethodAttribute());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_ShouldPopulatePaymentMethodAttribute() {
        when(sourceMock.getAttrName()).thenReturn(ATTR_NAME);
        when(sourceMock.getAttrValue()).thenReturn(ATTR_VALUE);
        when(sourceMock.getPaymentMethod()).thenReturn(PAYMENT_METHOD);

        final com.worldpay.internal.model.PaymentMethodAttribute targetMock = new com.worldpay.internal.model.PaymentMethodAttribute();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAttrName()).isEqualTo(ATTR_NAME);
        assertThat(targetMock.getAttrValue()).isEqualTo(ATTR_VALUE);
        assertThat(targetMock.getPaymentMethod()).isEqualTo(PAYMENT_METHOD);
    }
}
