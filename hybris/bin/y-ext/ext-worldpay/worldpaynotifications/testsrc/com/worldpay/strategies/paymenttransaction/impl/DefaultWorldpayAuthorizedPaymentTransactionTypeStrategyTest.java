package com.worldpay.strategies.paymenttransaction.impl;

import com.google.common.collect.ImmutableList;
import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAuthorizedPaymentTransactionTypeStrategyTest {


    @InjectMocks
    private DefaultWorldpayAuthorizedPaymentTransactionTypeStrategy testObj;

    @Mock
    private WorldpayOrderNotificationHandler worldpayOrderNotificationHandlerMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;

    @Mock
    private WorldpayOrderModificationModel worldpayOrderNotificationMock;
    @Mock
    private OrderModel orderMock;
    @Mock
    private PaymentTransactionModel paymentTransactionMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryMock1;

    @Before
    public void setUp() {
        when(orderMock.getPaymentTransactions()).thenReturn(ImmutableList.of(paymentTransactionMock));
        when(worldpayPaymentTransactionServiceMock.getNotPendingPaymentTransactionEntriesForType(paymentTransactionMock, AUTHORIZATION)).thenReturn(ImmutableList.of(paymentTransactionEntryMock1));
    }

    @Test
    public void processModificationMessage_WhenNotPendingTransactionEntries_ShouldProcessAndSetNonDefective() {
        testObj.processModificationMessage(orderMock, worldpayOrderNotificationMock);

        verify(worldpayOrderNotificationHandlerMock).setNonDefectiveAndProcessed(worldpayOrderNotificationMock);
    }

    @Test
    public void processModificationMessage_WhenPendingTransactionEntries_ShouldDoNothing() {
        when(worldpayPaymentTransactionServiceMock.getNotPendingPaymentTransactionEntriesForType(paymentTransactionMock, AUTHORIZATION)).thenReturn(Collections.emptyList());

        testObj.processModificationMessage(orderMock, worldpayOrderNotificationMock);

        verify(worldpayOrderNotificationHandlerMock, never()).setNonDefectiveAndProcessed(worldpayOrderNotificationMock);
    }
}
