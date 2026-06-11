package com.worldpay.cronjob;

import java.util.Collections;

import static de.hybris.platform.cronjob.enums.CronJobResult.SUCCESS;
import static de.hybris.platform.cronjob.enums.CronJobStatus.FINISHED;
import static de.hybris.platform.payment.dto.TransactionStatus.REVIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

@UnitTest
@ExtendWith(MockitoExtension.class)
class APMOrderTimeoutJobPerformableTest {

    private static final String ORDER_PROCESS_CODE = "orderProcessCode";

    private APMOrderTimeoutJobPerformable testObj;

    @Mock
    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDaoMock;
    @Mock
    private BusinessProcessService businessProcessServiceMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;

    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private CronJobModel cronJobModelMock;
    @Mock
    private OrderProcessModel orderProcessModelMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private TransactionStatus transactionStatusMock;
    @Mock
    private ModelService modelServiceMock;

    private final TransactionOperations transactionOperationsMock = new TransactionOperations() {
        @Override
        public <T> T execute(final TransactionCallback<T> transactionCallback) throws TransactionException {
            return transactionCallback.doInTransaction(transactionStatusMock);
        }
    };

    @BeforeEach
    void setUp() {
        testObj = new APMOrderTimeoutJobPerformable(worldpayPaymentTransactionDaoMock, businessProcessServiceMock, worldpayPaymentTransactionServiceMock, transactionOperationsMock);
        testObj.setModelService(modelServiceMock);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(worldpayPaymentTransactionDaoMock.findCancellablePendingAPMPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionModelMock));
        when(orderModelMock.getOrderProcess()).thenReturn(Collections.singletonList(orderProcessModelMock));
        when(orderProcessModelMock.getCode()).thenReturn(ORDER_PROCESS_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(paymentTransactionEntryModelMock));
    }

    @Test
    void testPerformShouldReturnSuccess() {
        final PerformResult result = testObj.perform(cronJobModelMock);

        assertEquals(SUCCESS, result.getResult());
        assertEquals(FINISHED, result.getStatus());

        verify(worldpayPaymentTransactionServiceMock).updateEntriesStatus(Collections.singletonList(paymentTransactionEntryModelMock), REVIEW.name());
        verify(modelServiceMock).save(paymentTransactionModelMock);
        verify(worldpayPaymentTransactionDaoMock).findCancellablePendingAPMPaymentTransactions();
        verify(businessProcessServiceMock).triggerEvent(ORDER_PROCESS_CODE + "_" + PaymentTransactionType.AUTHORIZATION);
    }
}
