package com.worldpay.orderprocess.strategies.cancel.impl;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayCancelWholeOrderDueToCancelNotificationStrategyTest {
    private static final String WORLDPAY_ORDER_CODE = "worldPayOrderCode";

    @InjectMocks
    private DefaultWorldpayCancelWholeOrderDueToCancelNotificationStrategy testObj;

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;

    @Mock
    private OrderProcessModel orderProcessModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryMock;

    @Before
    public void setUp() {
        when(orderProcessModelMock.getOrder()).thenReturn(orderModelMock);
        when(orderModelMock.getStatus()).thenReturn(OrderStatus.PAYMENT_CAPTURED);
        when(orderModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayPaymentTransactionServiceMock.createNotPendingCancelOrderTransactionEntry(paymentTransactionModelMock))
            .thenReturn(paymentTransactionEntryMock);
    }

    @Test
    public void cancelOrder_ShouldSetTheOrderStatusToCancelled() {
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);

        testObj.cancelOrder(orderProcessModelMock);

        verify(orderModelMock).setStatus(OrderStatus.CANCELLED);
        verify(paymentTransactionEntryMock).setPaymentTransaction(paymentTransactionModelMock);
        verify(modelServiceMock).save(paymentTransactionEntryMock);
        verify(modelServiceMock).save(orderModelMock);
    }

    @Test
    public void cancelOrder_WhenNoPaymentTransactionFound_ShouldSetTheOrderStatusToProcessingError() {
        testObj.cancelOrder(orderProcessModelMock);

        verify(orderModelMock).setStatus(OrderStatus.PROCESSING_ERROR);
        verify(paymentTransactionEntryMock, never()).setPaymentTransaction(paymentTransactionModelMock);
        verify(modelServiceMock).save(orderModelMock);
    }

    @Test
    public void cancelOrder_WhenOrderIsAlreadyCancelled_ShouldNotCreateTransaction() {
        when(orderModelMock.getStatus()).thenReturn(OrderStatus.CANCELLED);

        testObj.cancelOrder(orderProcessModelMock);

        verifyZeroInteractions(worldpayPaymentTransactionServiceMock);
        verifyZeroInteractions(modelServiceMock);
    }
}
