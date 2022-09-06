package com.worldpay.strategies.paymenttransaction.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayRefusedPaymentTransactionTypeStrategyTest {

    @InjectMocks
    private DefaultWorldpayRefusedPaymentTransactionTypeStrategy testObj;

    @Mock
    private OrderNotificationService orderNotificationServiceMock;

    @Mock
    private WorldpayOrderModificationModel worldpayOrderNotificationMock;
    @Mock
    private OrderModel orderMock;

    @Test
    public void processModificationMessage_ShouldProcessNotification() {
        when(worldpayOrderNotificationMock.getWorldpayOrderCode()).thenReturn("worldPayOrderCode");

        testObj.processModificationMessage(orderMock, worldpayOrderNotificationMock);

        verify(orderNotificationServiceMock).setNonDefectiveAndProcessed(worldpayOrderNotificationMock);
    }
}
