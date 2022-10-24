package com.worldpay.strategies.paymenttransaction.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.WorldpayOrderModificationRefundProcessStrategy;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.worldpay.worldpaynotifications.enums.DefectiveReason.INVALID_AUTHENTICATED_SHOPPER_ID;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayRefundedPaymentTransactionTypeStrategyTest {

    private static final String DESERIALISED_NOTIFICATION = "deserialized notification";

    @InjectMocks
    private DefaultWorldpayRefundedPaymentTransactionTypeStrategy testObj;

    @Mock
    private OrderNotificationService orderNotificationServiceMock;
    @Mock
    private WorldpayOrderModificationRefundProcessStrategy worldpayOrderModificationRefundProcessStrategyMock;

    @Mock
    private WorldpayOrderModificationModel worldpayOrderNotificationMock;
    @Mock
    private OrderModel orderMock;
    @Mock
    private OrderNotificationMessage orderNotificationMock;

    @Before
    public void setUp() {
        when(worldpayOrderNotificationMock.getOrderNotificationMessage()).thenReturn(DESERIALISED_NOTIFICATION);
        when(orderNotificationServiceMock.deserialiseNotification(DESERIALISED_NOTIFICATION)).thenReturn(orderNotificationMock);
        when(worldpayOrderModificationRefundProcessStrategyMock.processRefundFollowOn(orderMock, orderNotificationMock)).thenReturn(TRUE);
    }

    @Test
    public void processModificationMessage_WhenRefundFollowOnProcessed_ShouldProcessOrderNotification() throws WorldpayConfigurationException {
        testObj.processModificationMessage(orderMock, worldpayOrderNotificationMock);

        verify(orderNotificationServiceMock).processOrderNotificationMessage(orderNotificationMock, worldpayOrderNotificationMock);
    }

    @Test
    public void processModificationMessage_WhenRefundFollowOnFalse_ShouldSetProcessedAndDefectiveAndReason() {
        when(worldpayOrderModificationRefundProcessStrategyMock.processRefundFollowOn(orderMock, orderNotificationMock)).thenReturn(FALSE);

        testObj.processModificationMessage(orderMock, worldpayOrderNotificationMock);

        verify(orderNotificationServiceMock).setDefectiveModification(worldpayOrderNotificationMock, null, true);
    }

    @Test
    public void processModificationMessage_WhenExceptionProcessingNotification_ShouldSetProcessedAndDefective() throws WorldpayConfigurationException {
        doThrow(WorldpayConfigurationException.class).when(orderNotificationServiceMock).processOrderNotificationMessage(orderNotificationMock, worldpayOrderNotificationMock);

        testObj.processModificationMessage(orderMock, worldpayOrderNotificationMock);

        verify(orderNotificationServiceMock).setDefectiveReason(worldpayOrderNotificationMock, INVALID_AUTHENTICATED_SHOPPER_ID);
        verify(orderNotificationServiceMock).setDefectiveModification(worldpayOrderNotificationMock, null, true);
    }
}
