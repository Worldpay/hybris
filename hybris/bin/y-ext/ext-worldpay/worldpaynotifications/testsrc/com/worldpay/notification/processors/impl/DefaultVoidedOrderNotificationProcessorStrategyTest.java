package com.worldpay.notification.processors.impl;

import com.google.common.collect.ImmutableList;
import com.worldpay.data.Amount;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultVoidedOrderNotificationProcessorStrategyTest {

    @InjectMocks
    private DefaultVoidedOrderNotificationProcessorStrategy testObj;

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    ;
    @Mock
    private TransactionStatus transactionStatusMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private Amount amountMock;

    private TransactionOperations transactionOperationsMock = new TransactionOperations() {
        @Override
        public <T> T execute(final TransactionCallback<T> transactionCallback) throws TransactionException {
            return transactionCallback.doInTransaction(transactionStatusMock);
        }
    };

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(testObj, "transactionTemplate", transactionOperationsMock);
    }

    @Test
    public void shouldProcessAuthorisedNotificationAndSetRiskScore() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(ImmutableList.of(paymentTransactionEntryModelMock));
        when(orderNotificationMessageMock.getPaymentReply().getAmount()).thenReturn(amountMock);

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(worldpayPaymentTransactionServiceMock).updateEntriesStatus(ImmutableList.of(paymentTransactionEntryModelMock), ACCEPTED.name());
        verify(worldpayPaymentTransactionServiceMock).updateEntriesAmount(ImmutableList.of(paymentTransactionEntryModelMock), amountMock);
        verify(modelServiceMock).save(paymentTransactionModelMock);
    }

}
