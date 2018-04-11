package com.worldpay.ordercancel.impl.denialstrategies;

import com.google.common.collect.ImmutableList;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.ordercancel.DefaultOrderCancelDenialReason;
import de.hybris.platform.ordercancel.OrderCancelDenialReason;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayApmOrderCancelDenialStrategyTest {

    private static final String CANCEL_DENIAL_REASON_DESCRIPTION = "Order cannot be cancelled as payment was made through an APM or is still unknown.";
    @Spy
    private final WorldpayApmOrderCancelDenialStrategy testObj = new WorldpayApmOrderCancelDenialStrategy();

    @Mock
    private OrderCancelConfigModel orderCancelConfigModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private PrincipalModel principalModelMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;

    @Before
    public void setUp() throws Exception {
        final DefaultOrderCancelDenialReason orderCancelDenialReason = new DefaultOrderCancelDenialReason(5, CANCEL_DENIAL_REASON_DESCRIPTION);
        doReturn(orderCancelDenialReason).when(testObj).getReason();
    }

    @Test
    public void shouldReturnNullWhenThereIsNoReasonToDenyCancellation() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(ImmutableList.of(paymentTransactionModelMock));
        when(paymentTransactionModelMock.getInfo()).thenReturn(paymentInfoModelMock);
        when(paymentInfoModelMock.getIsApm()).thenReturn(false);
        when(paymentInfoModelMock.getPaymentType()).thenReturn("VISA-SSL");

        final OrderCancelDenialReason result = testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        assertThat(result).isNull();
    }

    @Test
    public void shouldReturnDenialReasonWhenPaymentInfoIsNull() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(ImmutableList.of(paymentTransactionModelMock));
        when(paymentTransactionModelMock.getInfo()).thenReturn(null);

        final DefaultOrderCancelDenialReason result = (DefaultOrderCancelDenialReason) testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        assertThat(result.getCode()).isEqualTo(5);
        assertThat(result.getDescription()).isEqualTo(CANCEL_DENIAL_REASON_DESCRIPTION);
    }

    @Test
    public void shouldReturnDenialReasonWhenPaymentInfoIsAPM() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(ImmutableList.of(paymentTransactionModelMock));
        when(paymentTransactionModelMock.getInfo()).thenReturn(paymentInfoModelMock);
        when(paymentInfoModelMock.getIsApm()).thenReturn(true);

        final DefaultOrderCancelDenialReason result = (DefaultOrderCancelDenialReason) testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        assertThat(result.getCode()).isEqualTo(5);
        assertThat(result.getDescription()).isEqualTo(CANCEL_DENIAL_REASON_DESCRIPTION);
    }

    @Test
    public void shouldReturnDenialReasonWhenPaymentInfoIsUnknown() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(ImmutableList.of(paymentTransactionModelMock));
        when(paymentTransactionModelMock.getInfo()).thenReturn(paymentInfoModelMock);
        when(paymentInfoModelMock.getIsApm()).thenReturn(false);
        when(paymentInfoModelMock.getPaymentType()).thenReturn("");

        final DefaultOrderCancelDenialReason result = (DefaultOrderCancelDenialReason) testObj.getCancelDenialReason(orderCancelConfigModelMock, orderModelMock, principalModelMock, false, false);

        assertThat(result.getCode()).isEqualTo(5);
        assertThat(result.getDescription()).isEqualTo(CANCEL_DENIAL_REASON_DESCRIPTION);
    }

}
