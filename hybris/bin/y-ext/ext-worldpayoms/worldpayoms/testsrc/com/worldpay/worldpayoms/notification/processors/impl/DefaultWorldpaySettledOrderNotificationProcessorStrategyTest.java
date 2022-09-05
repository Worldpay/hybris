package com.worldpay.worldpayoms.notification.processors.impl;

import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpaySettledOrderNotificationProcessorStrategyTest {

    @InjectMocks
    private DefaultWorldpaySettledOrderNotificationProcessorStrategy testObj;

    @Mock
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private TransactionStatus transactionStatusMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;

    private TransactionOperations transactionOperationsMock = new TransactionOperations() {
        @Override
        public <T> T execute(final TransactionCallback<T> transactionCallback) throws TransactionException {
            return transactionCallback.doInTransaction(transactionStatusMock);
        }
    };

    @Before
    public void setUp() {
        testObj.setTransactionTemplate(transactionOperationsMock);
    }

    @Test
    public void shouldCreateASettledPaymentTransactionEntry() {

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(worldpayPaymentTransactionServiceMock).createNotPendingSettledPaymentTransactionEntry(paymentTransactionModelMock, orderNotificationMessageMock);
    }
}
