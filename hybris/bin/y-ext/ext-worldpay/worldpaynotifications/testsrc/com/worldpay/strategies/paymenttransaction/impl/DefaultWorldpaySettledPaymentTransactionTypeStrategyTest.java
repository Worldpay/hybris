package com.worldpay.strategies.paymenttransaction.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.worldpaynotifications.enums.DefectiveReason.INVALID_AUTHENTICATED_SHOPPER_ID;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpaySettledPaymentTransactionTypeStrategyTest {

    private static final String DESERIALISED_NOTIFICATION = "deserialized notification";

    @InjectMocks
    private DefaultWorldpaySettledPaymentTransactionTypeStrategy testObj;

    @Mock
    private WorldpayOrderNotificationHandler worldpayOrderNotificationHandlerMock;
    @Mock
    private OrderModificationSerialiser orderModificationSerialiserMock;
    @Mock
    private OrderNotificationService orderNotificationServiceMock;

    @Mock
    private WorldpayOrderModificationModel worldpayOrderNotificationMock;
    @Mock
    private OrderModel orderMock;
    @Mock
    private OrderNotificationMessage orderNotificationMock;

    @Before
    public void setUp() {
        when(worldpayOrderNotificationMock.getOrderNotificationMessage()).thenReturn(DESERIALISED_NOTIFICATION);
        when(orderModificationSerialiserMock.deserialise(DESERIALISED_NOTIFICATION)).thenReturn(orderNotificationMock);
    }

    @Test
    public void processModificationMessage_WhenNoException_ShouldProcessOrderNotification() throws WorldpayConfigurationException {
        testObj.processModificationMessage(orderMock, worldpayOrderNotificationMock);

        verify(orderNotificationServiceMock).processOrderNotificationMessage(orderNotificationMock, worldpayOrderNotificationMock);
    }

    @Test
    public void processModificationMessage_WhenExceptionProcessingNotification_ShouldSetProcessedAndDefective() throws WorldpayConfigurationException {
        doThrow(WorldpayConfigurationException.class).when(orderNotificationServiceMock).processOrderNotificationMessage(orderNotificationMock, worldpayOrderNotificationMock);

        testObj.processModificationMessage(orderMock, worldpayOrderNotificationMock);

        verify(worldpayOrderNotificationHandlerMock).setDefectiveReason(worldpayOrderNotificationMock, INVALID_AUTHENTICATED_SHOPPER_ID);
        verify(worldpayOrderNotificationHandlerMock).setDefectiveModification(worldpayOrderNotificationMock, null, true);
    }
}
