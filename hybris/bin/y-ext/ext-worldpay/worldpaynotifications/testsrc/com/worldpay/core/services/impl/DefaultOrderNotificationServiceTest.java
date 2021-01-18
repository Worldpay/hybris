package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static com.worldpay.enums.order.AuthorisedStatus.REFUSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderNotificationServiceTest {

    private  static final String VALID_AUTHENTICATED_SHOPPER_ID = "validAuthenticatedShopperId";
    private static final String ORDER_CODE = "orderCode";

    @InjectMocks
    private DefaultOrderNotificationService testObj;

    @Mock
    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDaoMock;
    @Mock
    private OrderNotificationProcessorStrategy orderNotificationProcessorStrategyMock;
    @Mock
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategyMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private WorldpayOrderModificationModel worldpayOrderModificationMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private AbstractOrderModel orderMock;
    @Mock
    private UserModel userMock;

    @Before
    public void setUp() {
        final Map<AuthorisedStatus, OrderNotificationProcessorStrategy> journalTypeToNotificationProcessorMap = new HashMap<>();
        journalTypeToNotificationProcessorMap.put(AUTHORISED, orderNotificationProcessorStrategyMock);
        Whitebox.setInternalState(testObj, "journalTypeToNotificationProcessorStrategyMap", journalTypeToNotificationProcessorMap);

        when(orderMock.getUser()).thenReturn(userMock);
        when(worldpayAuthenticatedShopperIdStrategyMock.getAuthenticatedShopperId(userMock)).thenReturn(VALID_AUTHENTICATED_SHOPPER_ID);
        when(orderNotificationMessageMock.getTokenReply().getAuthenticatedShopperID()).thenReturn(VALID_AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void processOrderNotificationMessage_WhenNoProcessorFound_ShouldDoNothing() throws WorldpayConfigurationException {
        when(orderNotificationMessageMock.getJournalReply().getJournalType()).thenReturn(REFUSED);

        testObj.processOrderNotificationMessage(orderNotificationMessageMock, worldpayOrderModificationMock);

        verify(worldpayPaymentTransactionDaoMock, never()).findPaymentTransactionByRequestIdFromOrdersOnly(anyString());
        verify(orderNotificationProcessorStrategyMock, never()).processNotificationMessage(anyObject(), anyObject());
    }

    @Test
    public void processOrderNotificationMessage_ShouldInvokesProcessorForJournalType() throws WorldpayConfigurationException {
        when(orderNotificationMessageMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(orderNotificationMessageMock.getJournalReply().getJournalType()).thenReturn(AUTHORISED);
        when(worldpayPaymentTransactionDaoMock.findPaymentTransactionByRequestIdFromOrdersOnly(ORDER_CODE)).thenReturn(paymentTransactionModelMock);

        testObj.processOrderNotificationMessage(orderNotificationMessageMock, worldpayOrderModificationMock);

        verify(worldpayPaymentTransactionDaoMock).findPaymentTransactionByRequestIdFromOrdersOnly(ORDER_CODE);
        verify(orderNotificationProcessorStrategyMock).processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);
    }

    @Test
    public void isNotificationValid_WhenNotificationValid_ShouldReturnTrue() {
        boolean result = testObj.isNotificationValid(orderNotificationMessageMock, orderMock);

        assertThat(result).isTrue();
    }

    @Test
    public void isNotificationValid_WhenNotificationNotValid_ShouldReturnFalse() {
        when(orderNotificationMessageMock.getTokenReply().getAuthenticatedShopperID()).thenReturn("invalidAuthenticatedShopperId");

        boolean result = testObj.isNotificationValid(orderNotificationMessageMock, orderMock);

        assertThat(result).isFalse();
    }
}
