package com.worldpay.strategies.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.core.services.impl.DefaultWorldpayOrderModificationProcessService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.WorldpayPlaceOrderFromNotificationStrategy;
import com.worldpay.strategies.paymenttransaction.WorldpayPaymentTransactionTypeStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worldpay.worldpaynotifications.enums.DefectiveReason.NO_WORLDPAY_CODE_MATCHED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOrderModificationProcessServiceTest {

    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String SERIALIZED_JSON_STRING = "Serialized json string";
    private static final String PAYMENT_TRANSACTION_NOT_FOUND = "payment transaction not found";

    @InjectMocks
    private DefaultWorldpayOrderModificationProcessService testObj;

    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private WorldpayPlaceOrderFromNotificationStrategy worldpayPlaceOrderFromNotificationStrategyMock;
    @Mock
    private OrderNotificationService orderNotificationServiceMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private WorldpayOrderNotificationHandler worldpayOrderNotificationHandlerMock;
    @Mock
    private WorldpayPaymentTransactionTypeStrategy refundedPaymentTransactionTypeStrategyMock, refusedPaymentTransactionTypeStrategyMock, settledPaymentTransactionTypeStrategyMock, voidedPaymentTransactionTypeStrategyMock;

    @Mock
    private WorldpayOrderModificationModel worldpayOrderModificationMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private CartModel cartModelMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(testObj, "worldpayPaymentTransactionTypeStrategiesMap", ImmutableMap.of(
            REFUND_FOLLOW_ON, refundedPaymentTransactionTypeStrategyMock,
            SETTLED, settledPaymentTransactionTypeStrategyMock,
            REFUSED, refusedPaymentTransactionTypeStrategyMock,
            VOIDED, voidedPaymentTransactionTypeStrategyMock));
        ReflectionTestUtils.setField(testObj, "worldpayNotRefusedPaymentTransactionTypeStrategiesList", ImmutableList.of(
            REFUND_FOLLOW_ON, refundedPaymentTransactionTypeStrategyMock,
            SETTLED, settledPaymentTransactionTypeStrategyMock,
            VOIDED, voidedPaymentTransactionTypeStrategyMock));
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(orderNotificationServiceMock.getUnprocessedOrderModificationsByType(any(PaymentTransactionType.class))).thenReturn(singletonList(worldpayOrderModificationMock));
        when(worldpayOrderModificationMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayOrderModificationMock.getOrderNotificationMessage()).thenReturn(SERIALIZED_JSON_STRING);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(orderNotificationServiceMock.deserialiseNotification(SERIALIZED_JSON_STRING)).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(null);
    }

    @Test
    public void processOrderModificationMessages_WhenWorldpayOrderCodeRelatesToOrder_ShouldProcess() throws WorldpayConfigurationException {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock)).thenReturn(true);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(worldpayOrderNotificationHandlerMock).handleNotificationBusinessProcess(CAPTURE, worldpayOrderModificationMock, orderModelMock, orderNotificationMessageMock);
    }

    @Test
    public void processOrderModificationMessages_WhenOrderModificationTypeIsSETTLED_ShouldMarkOrderNotificationAsProcessedAndNotTriggerABusinessProcessEvent() throws WorldpayConfigurationException {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, SETTLED, orderModelMock)).thenReturn(Boolean.TRUE);

        final boolean result = testObj.processOrderModificationMessages(SETTLED);

        assertTrue(result);
        verify(settledPaymentTransactionTypeStrategyMock).processModificationMessage(orderModelMock, worldpayOrderModificationMock);
        verify(worldpayOrderNotificationHandlerMock, never()).handleNotificationBusinessProcess(CAPTURE, worldpayOrderModificationMock, orderModelMock, orderNotificationMessageMock);
    }

    @Test
    public void processOrderModificationMessages_WhenOrderModificationTypeIsVOIDED_ShouldMarkOrderNotificationAsProcessedAndNotTriggerABusinessProcessEvent() throws WorldpayConfigurationException {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, VOIDED, orderModelMock)).thenReturn(Boolean.TRUE);

        final boolean result = testObj.processOrderModificationMessages(VOIDED);

        assertTrue(result);
        verify(voidedPaymentTransactionTypeStrategyMock).processModificationMessage(orderModelMock, worldpayOrderModificationMock);
        verify(worldpayOrderNotificationHandlerMock, never()).handleNotificationBusinessProcess(CAPTURE, worldpayOrderModificationMock, orderModelMock, orderNotificationMessageMock);
    }

    @Test
    public void processOrderModificationMessages_WhenWorldpayOrderCodeRelatesToCart_ShouldDoNothing() throws WorldpayConfigurationException {
        when(paymentTransactionModelMock.getOrder()).thenReturn(cartModelMock);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(worldpayOrderNotificationHandlerMock, never()).handleNotificationBusinessProcess(CAPTURE, worldpayOrderModificationMock, orderModelMock, orderNotificationMessageMock);
        verify(worldpayOrderModificationMock, never()).setProcessed(Boolean.TRUE);
        verify(worldpayOrderModificationMock, never()).setDefective(any(Boolean.class));
    }

    @Test
    public void processOrderModificationMessages_WhenNoTransactionIsFoundButACartIsFound_shouldNotStartProcess() throws WorldpayConfigurationException {
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenThrow(new ModelNotFoundException(PAYMENT_TRANSACTION_NOT_FOUND));
        when(worldpayCartServiceMock.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(cartModelMock);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(worldpayOrderNotificationHandlerMock, never()).handleNotificationBusinessProcess(CAPTURE, worldpayOrderModificationMock, orderModelMock, orderNotificationMessageMock);
    }

    @Test
    public void processOrderModificationMessages_WhenACartWithTheOrderCodeIsFound_ShouldPlaceOrderInSession() {
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenThrow(new ModelNotFoundException(PAYMENT_TRANSACTION_NOT_FOUND));
        when(worldpayCartServiceMock.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(cartModelMock);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(worldpayPlaceOrderFromNotificationStrategyMock).placeOrderFromNotification(worldpayOrderModificationMock, cartModelMock);
    }

    @Test
    public void processOrderModificationMessages_WhenNoTransactionAndNoCartAreFound_ShouldMarkTheOrderModificationAsProcessedAndDefective() throws WorldpayConfigurationException {
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenThrow(new ModelNotFoundException(PAYMENT_TRANSACTION_NOT_FOUND));
        when(worldpayCartServiceMock.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenThrow(new ModelNotFoundException("order not found"));

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(orderNotificationServiceMock).setDefectiveReason(worldpayOrderModificationMock, NO_WORLDPAY_CODE_MATCHED);
        verify(orderNotificationServiceMock).setDefectiveModification(worldpayOrderModificationMock, null, false);
        verify(worldpayOrderNotificationHandlerMock, never()).handleNotificationBusinessProcess(CAPTURE, worldpayOrderModificationMock, orderModelMock, orderNotificationMessageMock);
    }

    @Test
    public void processOrderModificationMessages_WhenPreviousNotificationIsNotPending_ShouldProcessNotification() throws WorldpayConfigurationException {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock)).thenReturn(Boolean.TRUE);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(worldpayOrderNotificationHandlerMock).handleNotificationBusinessProcess(CAPTURE, worldpayOrderModificationMock, orderModelMock, orderNotificationMessageMock);
    }

    @Test
    public void processOrderModificationMessage_PreviousNotificationIsPending_ShouldNotProcessNotification() throws WorldpayConfigurationException {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock)).thenReturn(Boolean.FALSE);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(worldpayOrderNotificationHandlerMock, never()).handleNotificationBusinessProcess(CAPTURE, worldpayOrderModificationMock, orderModelMock, orderNotificationMessageMock);
    }

    @Test
    public void processOrderModificationMessages_ShouldProcessRefusedPaymentTransaction() {
        final boolean result = testObj.processOrderModificationMessages(REFUSED);

        assertTrue(result);
        verify(refusedPaymentTransactionTypeStrategyMock).processModificationMessage(null, worldpayOrderModificationMock);
    }

    @Test
    public void processOrderModificationMessages_ShouldProcessRefundNotification() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, REFUND_FOLLOW_ON, orderModelMock)).thenReturn(Boolean.TRUE);

        testObj.processOrderModificationMessages(REFUND_FOLLOW_ON);

        verify(refundedPaymentTransactionTypeStrategyMock).processModificationMessage(orderModelMock, worldpayOrderModificationMock);
    }

    @Test
    public void processOrderModificationMessages__WhenItIsACancelNotification_ShouldMarkNotificationAsProcessed() throws WorldpayConfigurationException {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CANCEL, orderModelMock)).thenReturn(Boolean.TRUE);
        when(orderNotificationServiceMock.deserialiseNotification(worldpayOrderModificationMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);

        testObj.processOrderModificationMessages(CANCEL);

        verify(worldpayOrderNotificationHandlerMock).handleNotificationBusinessProcess(CANCEL, worldpayOrderModificationMock, orderModelMock, orderNotificationMessageMock);
    }

    @Test
    public void processOrderModificationMessages_WhenWorldpayOrderCodeRelatesToCartWithAuthorizationTransactionType_ShouldDoNothing() throws WorldpayConfigurationException {
        when(paymentTransactionModelMock.getOrder()).thenReturn(cartModelMock);

        final boolean result = testObj.processOrderModificationMessages(AUTHORIZATION);

        assertTrue(result);
        verify(worldpayOrderNotificationHandlerMock, never()).handleNotificationBusinessProcess(AUTHORIZATION, worldpayOrderModificationMock, orderModelMock, orderNotificationMessageMock);
        verify(worldpayOrderModificationMock, never()).setProcessed(Boolean.TRUE);
        verify(worldpayOrderModificationMock, never()).setDefective(any(Boolean.class));
    }
}
