package com.worldpay.attributehandlers;

import com.worldpay.service.payment.WorldpayKlarnaService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayAPMPaymentInfoIsKlarnaPaymentTypeCodeDynamicAttributeHandlerTest {

    private static final String KLARNA_1 = "klarna1";
    private static final String NON_KLARNA = "nonKlarna";

    @Spy
    @InjectMocks
    private WorldpayAPMPaymentInfoIsKlarnaPaymentTypeCodeDynamicAttributeHandler testObj;

    @Mock
    private WorldpayKlarnaService worldpayKlarnaServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayAPMPaymentInfoModel paymentInfoModelMock;

    @Test
    public void get_shouldReturnTrueWhenKlarnaUtilsMockReturnsTrue() {
        when(paymentInfoModelMock.getApmConfiguration().getCode()).thenReturn(KLARNA_1);
        when(worldpayKlarnaServiceMock.isKlarnaPaymentType(KLARNA_1)).thenReturn(true);

        final Boolean result = testObj.get(paymentInfoModelMock);

        verify(worldpayKlarnaServiceMock).isKlarnaPaymentType(KLARNA_1);
        assertThat(result).isTrue();
    }

    @Test
    public void get_shouldReturnFalseWhenKlarnaUtilsMockReturnsFalse() {
        when(paymentInfoModelMock.getApmConfiguration().getCode()).thenReturn(NON_KLARNA);
        when(worldpayKlarnaServiceMock.isKlarnaPaymentType(NON_KLARNA)).thenReturn(false);

        final Boolean result = testObj.get(paymentInfoModelMock);

        verify(worldpayKlarnaServiceMock).isKlarnaPaymentType(NON_KLARNA);
        assertThat(result).isFalse();
    }

    @Test
    public void get_shouldReturnFalseWhenCodeIsNull() {
        when(paymentInfoModelMock.getApmConfiguration().getCode()).thenReturn(null);
        when(worldpayKlarnaServiceMock.isKlarnaPaymentType(null)).thenReturn(false);

        final Boolean result = testObj.get(paymentInfoModelMock);

        verify(worldpayKlarnaServiceMock).isKlarnaPaymentType(null);
        assertThat(result).isFalse();
    }

    @SuppressWarnings("java:S5778")
    @Test
    public void set_shouldReturnAUnsupportedOperationException() {
        assertThatThrownBy(() -> testObj.set(any(), any())).isInstanceOf(UnsupportedOperationException.class)
            .hasMessage("Attribute isKlarna is not writable");
    }
}
