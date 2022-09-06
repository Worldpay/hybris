package com.worldpay.worldpayoms.fulfilmentprocess.actions.order;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCancelPaymentActionTest {

    @InjectMocks
    private WorldpayCancelPaymentAction testObj;

    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private OrderProcessModel orderProcessModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private PaymentTransactionEntryModel cancellationPaymentTransactionEntryModelMock;

    @Before
    public void setUp() {
        when(orderProcessModelMock.getOrder()).thenReturn(orderModelMock);
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransactionModelMock));
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionModelMock, AUTHORIZATION)).thenReturn(singletonList(paymentTransactionEntryModelMock));
        when(paymentServiceMock.cancel(paymentTransactionEntryModelMock)).thenReturn(cancellationPaymentTransactionEntryModelMock);
    }

    @Test
    public void testExecuteShouldReturnOKWhenCancelIsSuccessful() {
        when(cancellationPaymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
        when(cancellationPaymentTransactionEntryModelMock.getTransactionStatusDetails()).thenReturn(TransactionStatusDetails.SUCCESFULL.name());

        final Transition result = testObj.executeAction(orderProcessModelMock);

        assertEquals(Transition.OK, result);
    }

    @Test
    public void testExecuteShouldReturnNOKWhenCancelIsNotSuccessful() {
        lenient().when(cancellationPaymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ERROR.name());
        lenient().when(cancellationPaymentTransactionEntryModelMock.getTransactionStatusDetails()).thenReturn(TransactionStatusDetails.COMMUNICATION_PROBLEM.name());

        final Transition result = testObj.executeAction(orderProcessModelMock);

        assertEquals(Transition.NOK, result);
    }

    @Test
    public void testExecuteShouldReturnNOKWhenOrderHasNoTransactions() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(emptyList());

        final Transition result = testObj.executeAction(orderProcessModelMock);

        assertEquals(Transition.NOK, result);
    }


    @Test
    public void testExecuteShouldReturnNOKWhenOrderNullTransaction() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(null));

        final Transition result = testObj.executeAction(orderProcessModelMock);

        assertEquals(Transition.NOK, result);
    }

    @Test
    public void testExecuteShouldReturnNOKWhenOrderHasNoTransactionEntries() {
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionModelMock, AUTHORIZATION)).thenReturn(emptyList());

        final Transition result = testObj.executeAction(orderProcessModelMock);

        assertEquals(Transition.NOK, result);
    }

    @Test
    public void testExecuteShouldReturnNOKWhenOrderHasNullTransactionEntry() {
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionModelMock, AUTHORIZATION)).thenReturn(singletonList(null));

        final Transition result = testObj.executeAction(orderProcessModelMock);

        assertEquals(Transition.NOK, result);
    }
}
