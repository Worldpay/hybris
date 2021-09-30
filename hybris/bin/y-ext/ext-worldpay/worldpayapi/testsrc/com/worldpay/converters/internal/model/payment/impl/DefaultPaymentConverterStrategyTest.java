package com.worldpay.converters.internal.model.payment.impl;

import com.worldpay.data.payment.Payment;
import com.worldpay.internal.model.IDEALSSL;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static com.worldpay.service.model.payment.PaymentType.IDEAL;
import static com.worldpay.service.model.payment.PaymentType.VISA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPaymentConverterStrategyTest {

    @InjectMocks
    private DefaultPaymentConverterStrategy testObj;

    @Mock
    private Map<String, Converter<Payment, Object>> paymentMapConvertersMock;

    @Mock
    private Converter<Payment, Object> idealConverterMock;
    @Mock
    private Payment idealPaymentMock;
    @Mock
    private IDEALSSL idealsslMock;

    @Before
    public void setUp() {
        paymentMapConvertersMock.put(IDEAL.getMethodCode(), idealConverterMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertPayment_WhenPaymentIsNull_ShouldThrowAnException() {
        testObj.convertPayment(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertPayment_WhenMapHasNotKey_ShouldThrowAnException() {
        when(idealPaymentMock.getPaymentType()).thenReturn(VISA.getMethodCode());

        testObj.convertPayment(idealPaymentMock);
    }

    @Test
    public void convertPayment_WhenMapHasKey_ShouldConvertPayment() {
        when(idealPaymentMock.getPaymentType()).thenReturn(IDEAL.getMethodCode());
        when(paymentMapConvertersMock.containsKey(IDEAL)).thenReturn(Boolean.TRUE);
        when(paymentMapConvertersMock.get(IDEAL)).thenReturn(idealConverterMock);
        when(idealConverterMock.convert(idealPaymentMock)).thenReturn(idealsslMock);

        final Object result = testObj.convertPayment(idealPaymentMock);

        assertThat(result).isEqualTo(idealsslMock);
    }
}
