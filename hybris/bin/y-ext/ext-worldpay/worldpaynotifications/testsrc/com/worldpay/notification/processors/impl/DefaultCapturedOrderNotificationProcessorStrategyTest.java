package com.worldpay.notification.processors.impl;

import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCapturedOrderNotificationProcessorStrategyTest {

    private DefaultCapturedOrderNotificationProcessorStrategy testObj;

    @Mock
    private ModelService modelServiceMock;

    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;

    private TransactionOperations transactionOperationsMock = new TransactionOperations() {
        @Override
        public <T> T execute(final TransactionCallback<T> transactionCallback) throws TransactionException {
            return transactionCallback.doInTransaction(transactionStatusMock);
        }
    };

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private TransactionStatus transactionStatusMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private PaymentReply paymentReplyMock;

    private List<PaymentTransactionEntryModel> pendingCaptureTransactionEntries;

    @Before
    public void setUp() {
        testObj = new DefaultCapturedOrderNotificationProcessorStrategy(transactionOperationsMock, modelServiceMock, worldpayPaymentTransactionServiceMock);
        pendingCaptureTransactionEntries = singletonList(paymentTransactionEntryModelMock);

        when(worldpayPaymentTransactionServiceMock.getPendingPaymentTransactionEntriesForType(paymentTransactionModelMock, CAPTURE)).thenReturn(pendingCaptureTransactionEntries);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(orderNotificationMessageMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getMethodCode()).thenReturn(PaymentType.VISA.getMethodCode());
    }

    @Test
    public void processNotificationMessage_shouldNotCreateCapturedPaymentTransactionEntryAndUpdateEntriesAmount_whenMessageIsNotAPM() {
        when(paymentTransactionModelMock.getInfo().getIsApm()).thenReturn(false);

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(worldpayPaymentTransactionServiceMock, never()).createCapturedPaymentTransactionEntry(paymentTransactionModelMock, orderNotificationMessageMock);
        verify(worldpayPaymentTransactionServiceMock).updateEntriesStatus(pendingCaptureTransactionEntries, ACCEPTED.name());
        verify(worldpayPaymentTransactionServiceMock).updateEntriesAmount(pendingCaptureTransactionEntries, orderNotificationMessageMock.getPaymentReply().getAmount());
        verify(modelServiceMock).save(paymentTransactionModelMock);
    }

    @Test
    public void processNotificationMessage_shouldCreateCapturedPaymentTransactionEntry_whenMessageIsAnAPMButIsNotKlarnaSSL() {
        when(paymentReplyMock.getMethodCode()).thenReturn(PaymentType.CASHU.getMethodCode());
        when(paymentTransactionModelMock.getInfo().getIsApm()).thenReturn(true);

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(worldpayPaymentTransactionServiceMock).createCapturedPaymentTransactionEntry(paymentTransactionModelMock, orderNotificationMessageMock);
        verify(worldpayPaymentTransactionServiceMock, never()).updateEntriesStatus(pendingCaptureTransactionEntries, ACCEPTED.name());
        verify(worldpayPaymentTransactionServiceMock, never()).updateEntriesAmount(pendingCaptureTransactionEntries, orderNotificationMessageMock.getPaymentReply().getAmount());
        verify(modelServiceMock, never()).save(paymentTransactionModelMock);
    }

    @Test
    public void processNotificationMessage_shouldNotCreateCapturedPaymentTransactionEntry_whenMessageIsAnAPMAndIsKlarnaSSL() {
        when(paymentReplyMock.getMethodCode()).thenReturn(PaymentType.KLARNASSL.getMethodCode());
        when(paymentTransactionModelMock.getInfo().getIsApm()).thenReturn(true);

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(worldpayPaymentTransactionServiceMock, never()).createCapturedPaymentTransactionEntry(paymentTransactionModelMock, orderNotificationMessageMock);
        verify(worldpayPaymentTransactionServiceMock).updateEntriesStatus(pendingCaptureTransactionEntries, ACCEPTED.name());
        verify(worldpayPaymentTransactionServiceMock).updateEntriesAmount(pendingCaptureTransactionEntries, orderNotificationMessageMock.getPaymentReply().getAmount());
        verify(modelServiceMock).save(paymentTransactionModelMock);
    }
}
