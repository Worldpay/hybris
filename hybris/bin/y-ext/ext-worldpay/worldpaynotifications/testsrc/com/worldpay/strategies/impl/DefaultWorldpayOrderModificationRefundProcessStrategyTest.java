package com.worldpay.strategies.impl;

import com.google.common.collect.ImmutableList;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;

import static de.hybris.platform.basecommerce.enums.ReturnStatus.CANCELED;
import static de.hybris.platform.basecommerce.enums.ReturnStatus.PAYMENT_REVERSED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CANCEL;
import static de.hybris.platform.payment.enums.PaymentTransactionType.REFUND_FOLLOW_ON;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOrderModificationRefundProcessStrategyTest {

    private static final String REFUND_REFERENCE = "refundReference";
    private static final String RETURN_PROCESS_CODE = "returnProcessCode";

    @InjectMocks
    private DefaultWorldpayOrderModificationRefundProcessStrategy testObj;

    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private ReturnRequestModel returnRequestMock1, returnRequestMock2;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryMock;
    @Mock
    private ReturnProcessModel refundWaitingReturnProcessMock1;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private BusinessProcessService businessProcessServiceMock;
    @Mock
    private ReturnProcessModel refundWaitingReturnProcessMock2;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private PaymentTransactionModel paymentTransactionMock;

    @Before
    public void setUp() {
        when(returnRequestMock1.getStatus()).thenReturn(PAYMENT_REVERSED);
        when(returnRequestMock2.getStatus()).thenReturn(PAYMENT_REVERSED);
        when(orderNotificationMessageMock.getPaymentReply().getRefundReference()).thenReturn(REFUND_REFERENCE);
        when(paymentTransactionEntryMock.getCode()).thenReturn(REFUND_REFERENCE);
        when(returnRequestMock1.getPaymentTransactionEntry()).thenReturn(paymentTransactionEntryMock);
        when(returnRequestMock2.getPaymentTransactionEntry()).thenReturn(paymentTransactionEntryMock);
        when(returnRequestMock1.getReturnProcess()).thenReturn(singletonList(refundWaitingReturnProcessMock1));
        when(refundWaitingReturnProcessMock1.getCode()).thenReturn(RETURN_PROCESS_CODE);
        when(orderModelMock.getReturnRequests()).thenReturn(singletonList(returnRequestMock1));
        when(orderModelMock.getPaymentTransactions()).thenReturn(ImmutableList.of(paymentTransactionMock));
    }

    @Test
    public void processRefundFollowOn_WhenNotificationIsREFUND_ShouldTriggerReturnProcess() {
        testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verify(businessProcessServiceMock).triggerEvent(RETURN_PROCESS_CODE + "_" + REFUND_FOLLOW_ON.toString());
        verify(returnRequestMock1).setStatus(PAYMENT_REVERSED);
        verify(modelServiceMock).save(returnRequestMock1);
    }

    @Test
    public void processRefundFollowOn_WhenNotificationIsREFUNDAndThereAreMultipleReturnRequest_ShouldTriggerReturnProcess() {
        when(orderModelMock.getReturnRequests()).thenReturn(Arrays.asList(returnRequestMock1, returnRequestMock2));

        testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verify(businessProcessServiceMock).triggerEvent(RETURN_PROCESS_CODE + "_" + REFUND_FOLLOW_ON.toString());
    }

    @Test
    public void processRefundFollowOn_WhenRefundReferenceIsNotEqualToTransactionEntryCode_ShouldNotTriggerReturnProcess() {
        when(paymentTransactionEntryMock.getCode()).thenReturn(REFUND_REFERENCE + "_INVALID");

        testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verifyZeroInteractions(businessProcessServiceMock);
    }

    @Test
    public void processRefundFollowOn_WhenNoReturnRequestsOnOrder_ShouldNotTriggerReturnProcess() {
        when(orderModelMock.getReturnRequests()).thenReturn(null);

        testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verifyZeroInteractions(businessProcessServiceMock);
        verifyZeroInteractions(modelServiceMock);
    }

    @Test
    public void processRefundFollowOn_WhenOneIsCancelled_ShouldTriggerCorrectProcess() {
        when(orderModelMock.getReturnRequests()).thenReturn(Arrays.asList(returnRequestMock1, returnRequestMock2));
        when(returnRequestMock1.getStatus()).thenReturn(CANCELED);
        when(returnRequestMock2.getStatus()).thenReturn(PAYMENT_REVERSED);
        when(returnRequestMock2.getReturnProcess()).thenReturn(singletonList(refundWaitingReturnProcessMock2));
        when(refundWaitingReturnProcessMock2.getCode()).thenReturn(RETURN_PROCESS_CODE);

        final boolean result = testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verify(businessProcessServiceMock).triggerEvent(RETURN_PROCESS_CODE + "_" + REFUND_FOLLOW_ON.toString());
        verifyNoMoreInteractions(businessProcessServiceMock);
        assertTrue(result);
    }

    @Test
    public void processRefundFollowOn_WhenRefundReferenceIsNull_ShouldReturnNotTriggerReturnProcess() {
        when(orderNotificationMessageMock.getPaymentReply().getRefundReference()).thenReturn(null);

        testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verifyZeroInteractions(businessProcessServiceMock);
    }

    @Test
    public void processRefundFollowOn_WhenOrderHasPendingCancelTransactionEntry_ShouldUpdateTheCancelEntry() {
        when(orderModelMock.getReturnRequests()).thenReturn(null);
        when(worldpayPaymentTransactionServiceMock.getPendingPaymentTransactionEntriesForType(paymentTransactionMock, CANCEL)).thenReturn(ImmutableList.of(paymentTransactionEntryMock));

        testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verify(paymentTransactionEntryMock).setTransactionStatus(TransactionStatus.REJECTED.name());
        verify(paymentTransactionEntryMock).setTransactionStatusDetails(TransactionStatusDetails.PROCESSOR_DECLINE.name());
        verify(paymentTransactionEntryMock).setPending(Boolean.FALSE);
        verify(paymentTransactionEntryMock).setAmount(BigDecimal.ZERO);
        verify(modelServiceMock).save(paymentTransactionEntryMock);
    }

}
