package com.worldpay.fulfilmentprocess.actions.order;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.jalo.order.Order;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import org.hibernate.validator.constraints.ModCheck;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;

import static de.hybris.platform.core.enums.OrderStatus.*;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCheckAuthorizeOrderPaymentActionTest {

    public static final String NOK = "NOK";
    public static final String OK = "OK";
    public static final String WAIT = "WAIT";

    @Spy
    @InjectMocks
    private WorldpayCheckAuthorizeOrderPaymentAction testObj = new WorldpayCheckAuthorizeOrderPaymentAction();

    @Mock
    private OrderProcessModel processMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private InvoicePaymentInfoModel invoicePaymentInfoMock;
    @Mock
    private PaymentTransactionModel paymentTransactionMock;
    @Mock
    private TimeService timeServiceMock;
    @Mock
    private Date dateMock;
    @Mock
    private OrderHistoryEntryModel orderHistoryEntryMock;

    @Before
    public void setup() throws Exception {
        when(processMock.getOrder()).thenReturn(orderModelMock);
        when(orderModelMock.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionMock));
    }

    @Test
    public void executeShouldReturnOKIfOrderPaymentInfoIsInvoice() throws Exception {
        when(orderModelMock.getPaymentInfo()).thenReturn(invoicePaymentInfoMock);

        final String result = testObj.execute(processMock);

        assertEquals(OK, result);
        verify(orderModelMock, never()).setStatus(PAYMENT_PENDING);
    }

    @Test
    public void executeShouldReturnNOKIfNotAllTransactionEntriesAuthorised() throws Exception {
        when(orderModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION)).thenReturn(false);

        final String result = testObj.execute(processMock);

        assertEquals(NOK, result);

        verify(orderModelMock).setStatus(PAYMENT_NOT_AUTHORIZED);
    }

    @Test
    public void executeShouldReturnWAITIfAllTransactionEntriesAcceptedButPending() throws Exception {
        when(orderModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isAuthorisedAmountCorrect(orderModelMock)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isPaymentTransactionPending(paymentTransactionMock, AUTHORIZATION)).thenReturn(true);

        final String result = testObj.execute(processMock);

        assertEquals(WAIT, result);
        verify(orderModelMock).setStatus(PAYMENT_PENDING);
    }

    @Test
    public void executeShouldReturnOKIfAllTransactionEntriesAcceptedAndNotPending() throws Exception {
        when(orderModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isAuthorisedAmountCorrect(orderModelMock)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isPaymentTransactionPending(paymentTransactionMock, AUTHORIZATION)).thenReturn(false);

        final String result = testObj.execute(processMock);

        assertEquals(OK, result);
        verify(modelServiceMock).save(orderModelMock);
        verify(orderModelMock).setStatus(PAYMENT_AUTHORIZED);
    }

    @Test
    public void executeShouldReturnNOKWhenAuthorisedAmountNotCorrect() throws Exception {
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isAuthorisedAmountCorrect(orderModelMock)).thenReturn(false);
        when(testObj.getTimeService()).thenReturn(timeServiceMock);
        when(modelServiceMock.create(OrderHistoryEntryModel.class)).thenReturn(orderHistoryEntryMock);
        when(timeServiceMock.getCurrentTime()).thenReturn(dateMock);

        final String result = testObj.execute(processMock);

        assertEquals(NOK, result);

        verify(orderModelMock).setStatus(CHECKED_INVALID);
    }

    @Test
    public void executeShouldReturnOKWhenAuthorisedAmountIsCorrect() throws Exception {
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isAuthorisedAmountCorrect(orderModelMock)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isPaymentTransactionPending(paymentTransactionMock, AUTHORIZATION)).thenReturn(false);

        final String result = testObj.execute(processMock);

        assertEquals(OK, result);

        verify(orderModelMock).setStatus(PAYMENT_AUTHORIZED);
    }
}