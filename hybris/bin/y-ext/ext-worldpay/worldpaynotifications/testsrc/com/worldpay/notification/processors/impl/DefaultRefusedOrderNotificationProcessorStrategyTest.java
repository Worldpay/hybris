package com.worldpay.notification.processors.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Collections;

import static de.hybris.platform.payment.dto.TransactionStatus.REJECTED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRefusedOrderNotificationProcessorStrategyTest {

    private static final String ORDER_CODE = "orderCode";

    private DefaultRefusedOrderNotificationProcessorStrategy testObj;

    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private TransactionStatus transactionStatusMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private com.worldpay.data.PaymentReply paymentReplyMock;
    @Mock
    private com.worldpay.data.Amount amountMock;

    private TransactionOperations transactionOperationsMock = new TransactionOperations() {
        @Override
        public <T> T execute(final TransactionCallback<T> transactionCallback) throws TransactionException {
            return transactionCallback.doInTransaction(transactionStatusMock);
        }
    };

    @Before
    public void setUp() {
        testObj = new DefaultRefusedOrderNotificationProcessorStrategy(transactionOperationsMock, worldpayPaymentInfoServiceMock, worldpayPaymentTransactionServiceMock, modelServiceMock);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(paymentTransactionEntryModelMock));
        when(orderNotificationMessageMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAmount()).thenReturn(amountMock);
        when(orderModelMock.getCode()).thenReturn(ORDER_CODE);
    }

    @Test
    public void shouldProcessRefusedNotification() throws WorldpayConfigurationException {
        when(orderModelMock.getStatus()).thenReturn(OrderStatus.PAYMENT_PENDING);

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(paymentTransactionModelMock).getOrder();
        verify(modelServiceMock).save(paymentTransactionModelMock);
        verify(worldpayPaymentTransactionServiceMock).updateEntriesStatus(Collections.singletonList(paymentTransactionEntryModelMock), REJECTED.name());
        verify(worldpayPaymentTransactionServiceMock).updateEntriesAmount(Collections.singletonList(paymentTransactionEntryModelMock), amountMock);
        verify(worldpayPaymentInfoServiceMock).setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);
    }

    @Test
    public void shouldProcessRefusedNotificationAndShouldNOTTriggerAnyEventIfOrderStatusIsNOTPaymentPending() throws WorldpayConfigurationException {
        when(orderModelMock.getStatus()).thenReturn(OrderStatus.CREATED);

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(paymentTransactionModelMock).getOrder();
        verify(worldpayPaymentInfoServiceMock, never()).setPaymentInfoModel(any(PaymentTransactionModel.class), any(AbstractOrderModel.class), any(OrderNotificationMessage.class));
        verify(modelServiceMock, never()).save(paymentTransactionModelMock);
        verify(worldpayPaymentTransactionServiceMock, never()).updateEntriesStatus(anyListOf(PaymentTransactionEntryModel.class), anyString());
    }
}
