package com.worldpay.fulfilmentprocess.actions.order;


import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
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

import java.util.Arrays;
import java.util.Collections;

import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_CAPTURED;
import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_NOT_CAPTURED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayTakePaymentActionTest {

    private static final String OK = "OK";
    private static final String NOK = "NOK";
    private static final String WAIT = "WAIT";

    @InjectMocks
    private WorldpayTakePaymentAction testObj;

    @Mock
    private OrderProcessModel processMock;
    @Mock
    private OrderModel orderMock;
    @Mock
    private PaymentTransactionModel paymentTransactionMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock2;
    @Mock
    private PaymentInfoModel paymentInfoMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModelMock;

    @Before
    public void setUp() {
        when(processMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionMock));
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionMock, CAPTURE)).thenReturn(Collections.singletonList(paymentTransactionEntryModelMock));
        when(paymentTransactionMock.getInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getIsApm()).thenReturn(false);
    }

    @Test
    public void execute_WhenExistingPendingCaptureEntry_ShouldReturnWAIT() {
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderMock, CAPTURE)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isPaymentTransactionPending(paymentTransactionMock, CAPTURE)).thenReturn(true);

        final String result = testObj.execute(processMock);

        assertEquals(WAIT, result);
    }

    @Test
    public void execute_WhenCaptureIsNotAccepted_ShouldReturnNOK() {
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderMock, CAPTURE)).thenReturn(false);

        final String result = testObj.execute(processMock);

        assertEquals(NOK, result);
        verify(orderMock).setStatus(PAYMENT_NOT_CAPTURED);
        verify(modelServiceMock).save(orderMock);
    }

    @Test
    public void execute_WhenCaptureNotPendingAndAccepted_ShouldReturnOK() {
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderMock, CAPTURE)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isPaymentTransactionPending(paymentTransactionMock, CAPTURE)).thenReturn(false);
        lenient().when(paymentServiceMock.capture(paymentTransactionMock)).thenReturn(paymentTransactionEntryModelMock);
        lenient().when(paymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());

        final String result = testObj.execute(processMock);

        assertEquals(OK, result);
        verify(orderMock).setStatus(PAYMENT_CAPTURED);
        verify(modelServiceMock).save(orderMock);
    }

    @Test
    public void execute_WhenThereIsNotCapture_ShouldReturnWAITAndUpdatesOrderStatus() {
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionMock, CAPTURE)).thenReturn(Collections.emptyList());
        lenient().when(paymentServiceMock.capture(paymentTransactionMock)).thenReturn(paymentTransactionEntryModelMock);
        lenient().when(paymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());

        final String result = testObj.execute(processMock);

        assertEquals(WAIT, result);
        verify(paymentServiceMock).capture(paymentTransactionMock);
    }

    @Test
    public void execute_WhenThereIsMoreThanOneTransactionEntry_ShouldReturnNOK() {
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionMock, CAPTURE)).
            thenReturn(Arrays.asList(paymentTransactionEntryModelMock, paymentTransactionEntryModelMock2));
        lenient().when(paymentServiceMock.capture(paymentTransactionMock)).thenReturn(paymentTransactionEntryModelMock);
        lenient().when(paymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());

        final String result = testObj.execute(processMock);

        assertEquals(NOK, result);
        verify(orderMock).setStatus(PAYMENT_NOT_CAPTURED);
        verify(modelServiceMock).save(orderMock);
    }

    @Test
    public void execute_WhenNoCaptureEntriesAndPaymentInfoIsAPM_ShouldNotCaptureAndShouldWait() {
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionMock, CAPTURE)).thenReturn(Collections.emptyList());
        when(paymentTransactionMock.getInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getIsApm()).thenReturn(true);

        final String result = testObj.execute(processMock);

        assertEquals(WAIT, result);
        verify(paymentServiceMock, never()).capture(any());
    }

    @Test
    public void execute_WhenPaymentInfoIsKlarna_ShouldIssueACapture() {
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionMock, CAPTURE)).thenReturn(Collections.emptyList());
        when(paymentTransactionMock.getInfo()).thenReturn(worldpayAPMPaymentInfoModelMock);
        when(worldpayAPMPaymentInfoModelMock.getApmConfiguration().getCode()).thenReturn(PaymentType.KLARNAV2SSL.getMethodCode());

        final String result = testObj.execute(processMock);

        assertEquals(WAIT, result);
        verify(paymentServiceMock).capture(any());
    }
}
