package com.worldpay.notification.processors.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.data.Amount;
import com.worldpay.data.PaymentReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.payment.WorldpayFraudSightStrategy;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAuthorisedOrderNotificationProcessorStrategyTest {

    private DefaultAuthorisedOrderNotificationProcessorStrategy testObj;

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private WorldpayFraudSightStrategy worldpayFraudSightStrategyMock;

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
    @Mock
    private Amount amountMock;
    @Mock
    private BaseSiteModel siteMock;

    private List<PaymentTransactionEntryModel> pendingAuthorizationTransactionEntries;
    private TransactionOperations transactionOperationsMock = new TransactionOperations() {
        @Override
        public <T> T execute(final TransactionCallback<T> transactionCallback) throws TransactionException {
            return transactionCallback.doInTransaction(transactionStatusMock);
        }
    };

    @Before
    public void setUp() {
        testObj = new DefaultAuthorisedOrderNotificationProcessorStrategy(modelServiceMock, transactionOperationsMock, worldpayPaymentTransactionServiceMock, worldpayPaymentInfoServiceMock, worldpayOrderServiceMock, worldpayFraudSightStrategyMock);
        pendingAuthorizationTransactionEntries = Arrays.asList(paymentTransactionEntryModelMock, paymentTransactionEntryModelMock);
        when(worldpayPaymentTransactionServiceMock.getPendingPaymentTransactionEntriesForType(paymentTransactionModelMock, AUTHORIZATION)).thenReturn(pendingAuthorizationTransactionEntries);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(orderNotificationMessageMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAmount()).thenReturn(amountMock);
        when(worldpayOrderServiceMock.convertAmount(amountMock)).thenReturn(BigDecimal.valueOf(10.6d));
        when(orderModelMock.getSite()).thenReturn(siteMock);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
    }

    @Test
    public void processNotificationMessage_WhenFSDisabled_shouldProcessAuthorisedNotificationAndSetRiskScore() throws WorldpayConfigurationException {
        when(worldpayFraudSightStrategyMock.isFraudSightEnabled(siteMock)).thenReturn(false);

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(worldpayPaymentTransactionServiceMock).updateEntriesStatus(pendingAuthorizationTransactionEntries, ACCEPTED.name());
        verify(worldpayPaymentTransactionServiceMock).updateEntriesAmount(pendingAuthorizationTransactionEntries, paymentReplyMock.getAmount());
        verify(paymentTransactionModelMock).setPlannedAmount(BigDecimal.valueOf(10.6d));
        verify(worldpayPaymentInfoServiceMock).setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);
        verify(paymentTransactionModelMock).setApmOpen(false);
        verify(modelServiceMock).save(paymentTransactionModelMock);
        verify(worldpayPaymentTransactionServiceMock).addRiskScore(paymentTransactionModelMock, paymentReplyMock);
        verify(worldpayFraudSightStrategyMock, never()).addFraudSight(paymentTransactionModelMock, paymentReplyMock);
    }

    @Test
    public void processNotificationMessage_WhenFSEnabled_shouldProcessAuthorisedNotificationAndSetRiskScoreAndSetFraudSight() throws WorldpayConfigurationException {
        when(worldpayFraudSightStrategyMock.isFraudSightEnabled(siteMock)).thenReturn(true);

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(worldpayPaymentTransactionServiceMock).updateEntriesStatus(pendingAuthorizationTransactionEntries, ACCEPTED.name());
        verify(worldpayPaymentTransactionServiceMock).updateEntriesAmount(pendingAuthorizationTransactionEntries, paymentReplyMock.getAmount());
        verify(paymentTransactionModelMock).setPlannedAmount(BigDecimal.valueOf(10.6d));
        verify(worldpayPaymentInfoServiceMock).setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);
        verify(paymentTransactionModelMock).setApmOpen(false);
        verify(modelServiceMock).save(paymentTransactionModelMock);
        verify(worldpayPaymentTransactionServiceMock).addRiskScore(paymentTransactionModelMock, paymentReplyMock);
        verify(worldpayFraudSightStrategyMock).addFraudSight(paymentTransactionModelMock, paymentReplyMock);
    }

    @Test
    public void shouldSetAAvFieldsOnTransactionEntry() {

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(worldpayPaymentTransactionServiceMock, times(2)).addAavFields(paymentTransactionEntryModelMock, paymentReplyMock);
    }
}
