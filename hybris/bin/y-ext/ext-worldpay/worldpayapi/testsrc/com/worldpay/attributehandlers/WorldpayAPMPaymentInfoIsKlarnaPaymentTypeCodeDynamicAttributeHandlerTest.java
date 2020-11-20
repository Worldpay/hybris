package com.worldpay.attributehandlers;

import com.worldpay.klarna.WorldpayKlarnaUtils;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
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
    private WorldpayKlarnaUtils worldpayKlarnaUtilsMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayAPMPaymentInfoModel paymentInfoModelMock;

    @Test
    public void get_shouldReturnTrueWhenKlarnaUtilsMockReturnsTrue() {
        when(paymentInfoModelMock.getApmConfiguration().getCode()).thenReturn(KLARNA_1);
        when(worldpayKlarnaUtilsMock.isKlarnaPaymentType(KLARNA_1)).thenReturn(true);

        final Boolean result = testObj.get(paymentInfoModelMock);

        verify(worldpayKlarnaUtilsMock).isKlarnaPaymentType(KLARNA_1);
        assertThat(result).isTrue();
    }

    @Test
    public void get_shouldReturnFalseWhenKlarnaUtilsMockReturnsFalse() {
        when(paymentInfoModelMock.getApmConfiguration().getCode()).thenReturn(NON_KLARNA);
        when(worldpayKlarnaUtilsMock.isKlarnaPaymentType(NON_KLARNA)).thenReturn(false);

        final Boolean result = testObj.get(paymentInfoModelMock);

        verify(worldpayKlarnaUtilsMock).isKlarnaPaymentType(NON_KLARNA);
        assertThat(result).isFalse();
    }

    @Test
    public void get_shouldReturnFalseWhenCodeIsNull() {
        when(paymentInfoModelMock.getApmConfiguration().getCode()).thenReturn(null);
        when(worldpayKlarnaUtilsMock.isKlarnaPaymentType(null)).thenReturn(false);

        final Boolean result = testObj.get(paymentInfoModelMock);

        verify(worldpayKlarnaUtilsMock).isKlarnaPaymentType(null);
        assertThat(result).isFalse();
    }

    @SuppressWarnings("java:S5778")
    @Test
    public void set_shouldReturnAUnsupportedOperationException() {
        assertThatThrownBy(() -> testObj.set(any(), any())).isInstanceOf(UnsupportedOperationException.class)
            .hasMessage("Attribute isKlarna is not writable");
    }
}
