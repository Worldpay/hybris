package com.worldpay.strategies.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.dao.OrderModificationDao;
import com.worldpay.dao.ProcessDefinitionDao;
import com.worldpay.notification.processors.WorldpayOrderNotificationHandler;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.WorldpayOrderModificationRefundProcessStrategy;
import com.worldpay.strategies.WorldpayPlaceOrderFromNotificationStrategy;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static com.worldpay.worldpaynotifications.enums.DefectiveReason.*;
import static de.hybris.platform.core.enums.OrderStatus.CANCELLED;
import static de.hybris.platform.core.enums.OrderStatus.FRAUD_CHECKED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOrderModificationProcessStrategyTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String AUTHORISE_PROCESS_CODE = "authoriseProcessCode";
    private static final String SERIALIZED_JSON_STRING = "Serialized json string";
    private static final String CAPTURE_BPM_PROCESS_CODE = "captureBMPProcessCode";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_REPLY_AUTHENTICATED_SHOPPER_ID = "tokenReplyAuthenticatedShopperId";
    private static final String PAYMENT_TRANSACTION_NOT_FOUND = "payment transaction not found";

    @InjectMocks
    private DefaultWorldpayOrderModificationProcessStrategy testObj;

    @Mock
    private OrderModificationDao orderModificationDaoMock;
    @Mock
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategyMock;
    @Mock
    private WorldpayOrderModificationRefundProcessStrategy worldpayOrderModificationRefundProcessStrategyMock;
    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private WorldpayPlaceOrderFromNotificationStrategy worldpayPlaceOrderFromNotificationStrategyMock;
    @Mock
    private ProcessDefinitionDao processDefinitionDaoMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private BusinessProcessService businessProcessServiceMock;
    @Mock
    private OrderNotificationService orderNotificationServiceMock;
    @Mock
    private OrderModificationSerialiser orderModificationSerialiserMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private WorldpayOrderNotificationHandler worldpayOrderNotificationHandlerMock;

    @Mock
    private WorldpayOrderModificationModel orderModificationModelMock;
    @Mock
    private BusinessProcessModel captureWaitingProcessMock, authoriseWaitingProcessMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private CartModel cartModelMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private Set<OrderStatus> nonTriggeringOrderStatusesMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryMock;
    @Mock
    private TokenReply tokenReplyMock;
    @Mock
    private CustomerModel customerModelMock;

    @Before
    public void setUp() {
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(nonTriggeringOrderStatusesMock.contains(CANCELLED)).thenReturn(true);
        when(orderModificationDaoMock.findUnprocessedOrderModificationsByType(CAPTURE)).thenReturn(singletonList(orderModificationModelMock));
        when(orderModificationDaoMock.findUnprocessedOrderModificationsByType(CANCEL)).thenReturn(singletonList(orderModificationModelMock));
        when(orderModificationDaoMock.findUnprocessedOrderModificationsByType(AUTHORIZATION)).thenReturn(singletonList(orderModificationModelMock));
        when(orderModificationDaoMock.findUnprocessedOrderModificationsByType(REFUND_FOLLOW_ON)).thenReturn(singletonList(orderModificationModelMock));
        when(orderModificationDaoMock.findUnprocessedOrderModificationsByType(SETTLED)).thenReturn(singletonList(orderModificationModelMock));
        when(processDefinitionDaoMock.findWaitingOrderProcesses(ORDER_CODE, CAPTURE)).thenReturn(singletonList(captureWaitingProcessMock));
        when(processDefinitionDaoMock.findWaitingOrderProcesses(ORDER_CODE, AUTHORIZATION)).thenReturn(singletonList(authoriseWaitingProcessMock));
        when(orderModificationModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderModificationModelMock.getOrderNotificationMessage()).thenReturn(SERIALIZED_JSON_STRING);
        when(captureWaitingProcessMock.getCode()).thenReturn(CAPTURE_BPM_PROCESS_CODE);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(orderModificationSerialiserMock.deserialise(SERIALIZED_JSON_STRING)).thenReturn(orderNotificationMessageMock);
        when(orderModelMock.getCode()).thenReturn(ORDER_CODE);
        when(orderModelMock.getUser()).thenReturn(customerModelMock);
        when(worldpayAuthenticatedShopperIdStrategyMock.getAuthenticatedShopperId(customerModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(tokenReplyMock.getAuthenticatedShopperID()).thenReturn(TOKEN_REPLY_AUTHENTICATED_SHOPPER_ID);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(null);
    }

    @Test
    public void processOrderModificationMessagesShouldProcessIfWorldpayOrderCodeRelatesToOrder() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock)).thenReturn(true);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(orderNotificationServiceMock).processOrderNotificationMessage(orderNotificationMessageMock);
        verify(businessProcessServiceMock).triggerEvent(CAPTURE_BPM_PROCESS_CODE + "_" + CAPTURE.getCode());
        verify(worldpayOrderNotificationHandlerMock).setNonDefectiveAndProcessed(orderModificationModelMock);
    }

    @Test
    public void processOrderModificationMessagesShouldMarkOrderNotificationAsProcessedAndNotTriggerABusinessProcessEventWhenOrderModificationTypeIsSETTLED() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, SETTLED, orderModelMock)).thenReturn(Boolean.TRUE);

        final boolean result = testObj.processOrderModificationMessages(SETTLED);

        assertTrue(result);
        verify(orderNotificationServiceMock).processOrderNotificationMessage(orderNotificationMessageMock);
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(worldpayOrderNotificationHandlerMock).setNonDefectiveAndProcessed(orderModificationModelMock);
    }

    @Test
    public void processOrderModificationMessagesShouldDoNothingIfWorldpayOrderCodeRelatesToCart() {
        when(paymentTransactionModelMock.getOrder()).thenReturn(cartModelMock);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(orderModificationModelMock, never()).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefective(any(Boolean.class));
        verify(modelServiceMock, never()).save(any(WorldpayOrderModificationModel.class));
    }

    @Test
    public void processOrderModificationMessagesShouldDoNothingIfNotSingleBusinessProcessFound() {
        when(processDefinitionDaoMock.findWaitingOrderProcesses(ORDER_CODE, CAPTURE)).thenReturn(asList(authoriseWaitingProcessMock, captureWaitingProcessMock));
        when(paymentTransactionModelMock.getOrder()).thenReturn(cartModelMock);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(orderModificationModelMock, never()).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefective(any(Boolean.class));
        verify(modelServiceMock, never()).save(any(WorldpayOrderModificationModel.class));
    }

    @Test
    public void shouldMarkTheOrderModificationAsProcessedAndNotDefectiveWhenNoTransactionIsFoundButACartIsFound() {
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenThrow(new ModelNotFoundException(PAYMENT_TRANSACTION_NOT_FOUND));
        when(worldpayCartServiceMock.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(cartModelMock);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);

        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(processDefinitionDaoMock, never()).findWaitingOrderProcesses(anyString(), any(PaymentTransactionType.class));
    }

    @Test
    public void shouldPlaceOrderInSessionWhenACartWithTheOrderCodeIsFound() {
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenThrow(new ModelNotFoundException(PAYMENT_TRANSACTION_NOT_FOUND));
        when(worldpayCartServiceMock.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenReturn(cartModelMock);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(worldpayPlaceOrderFromNotificationStrategyMock).placeOrderFromNotification(orderModificationModelMock, cartModelMock);
    }

    @Test
    public void shouldMarkTheOrderModificationAsProcessedAndDefectiveWhenNoTransactionAndNoCartAreFound() {
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenThrow(new ModelNotFoundException(PAYMENT_TRANSACTION_NOT_FOUND));
        when(worldpayCartServiceMock.findCartByWorldpayOrderCode(WORLDPAY_ORDER_CODE)).thenThrow(new ModelNotFoundException("order not found"));

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(worldpayOrderNotificationHandlerMock).setDefectiveReason(orderModificationModelMock, NO_WORLDPAY_CODE_MATCHED);
        verify(worldpayOrderNotificationHandlerMock).setDefectiveModification(orderModificationModelMock, null, false);
        verify(processDefinitionDaoMock, never()).findWaitingOrderProcesses(anyString(), any(PaymentTransactionType.class));
    }

    @Test
    public void processOrderModificationMessageShouldProcessNotificationIfPreviousNotificationIsNotPending() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock)).thenReturn(Boolean.TRUE);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        verify(businessProcessServiceMock).triggerEvent(anyString());
        verify(worldpayOrderNotificationHandlerMock).setNonDefectiveAndProcessed(orderModificationModelMock);
        verify(processDefinitionDaoMock).findWaitingOrderProcesses(anyString(), any(PaymentTransactionType.class));
        assertTrue(result);
    }

    @Test
    public void processOrderModificationMessageShouldNotProcessNotificationIfPreviousNotificationIsPending() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock)).thenReturn(Boolean.FALSE);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(modelServiceMock, never()).save(any(WorldpayOrderModificationModel.class));
        verify(processDefinitionDaoMock, never()).findWaitingOrderProcesses(anyString(), any(PaymentTransactionType.class));
    }

    @Test
    public void processOrderModificationMessageShouldProcessCancelPaymentTransaction() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CANCEL, orderModelMock)).thenReturn(Boolean.TRUE);
        when(authoriseWaitingProcessMock.getCode()).thenReturn(AUTHORISE_PROCESS_CODE);

        final boolean result = testObj.processOrderModificationMessages(CANCEL);

        assertTrue(result);
        verify(worldpayOrderNotificationHandlerMock).setNonDefectiveAndProcessed(orderModificationModelMock);
    }

    @Test
    public void processOrderModificationMessageShouldSetOrderModificationAsDefectiveWhenAnyExceptionIsThrown() {
        doThrow(new IllegalArgumentException(EXCEPTION_MESSAGE)).when(orderNotificationServiceMock).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        final boolean result = testObj.processOrderModificationMessages(AUTHORIZATION);

        assertFalse(result);
        verify(worldpayOrderNotificationHandlerMock).setDefectiveReason(orderModificationModelMock, PROCESSING_ERROR);
        verify(worldpayOrderNotificationHandlerMock).setDefectiveModification(eq(orderModificationModelMock), any(Exception.class), eq(true));
    }

    @Test
    public void shouldNotWakeUpOrderProcessWhenOrderStatusIsCancelled() {
        when(orderModelMock.getStatus()).thenReturn(CANCELLED);
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
    }

    @Test
    public void shouldWakeUpOrderProcessWhenOrderStatusIsNotCancelled() {
        when(orderModelMock.getStatus()).thenReturn(FRAUD_CHECKED);
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock)).thenReturn(Boolean.TRUE);

        testObj.processOrderModificationMessages(CAPTURE);

        verify(businessProcessServiceMock).triggerEvent(anyString());
    }

    @Test
    public void shouldMarkAsProcessedWhenOrderNotificationIsAUTHAndTransactionEntryIsNotPending() {
        when(worldpayPaymentTransactionServiceMock.getNotPendingPaymentTransactionEntriesForType(paymentTransactionModelMock, AUTHORIZATION)).thenReturn(singletonList(paymentTransactionEntryMock));

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(worldpayOrderNotificationHandlerMock).setNonDefectiveAndProcessed(orderModificationModelMock);
        verify(orderNotificationServiceMock, never()).processOrderNotificationMessage(orderNotificationMessageMock);
    }

    @Test
    public void markNotificationAsDefectiveWhenContainsTokenReplyWithNonMatchingAuthenticatedShopperId() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationSerialiserMock.deserialise(orderModificationModelMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderNotificationServiceMock, never()).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(worldpayOrderNotificationHandlerMock).setDefectiveReason(orderModificationModelMock, DefectiveReason.INVALID_AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void markNotificationAsDefectiveWithInvalidAuthenticatedShopperIdReasonWhenContainsTokenReplyWithNonMatchingAuthenticatedShopperId() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationSerialiserMock.deserialise(orderModificationModelMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderNotificationServiceMock, never()).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(worldpayOrderNotificationHandlerMock).setDefectiveReason(orderModificationModelMock, INVALID_AUTHENTICATED_SHOPPER_ID);
        verify(worldpayOrderNotificationHandlerMock).setDefectiveModification(orderModificationModelMock, null, true);
    }

    @Test
    public void notificationShouldNotBeMarkedAsDefectiveWhenTokenReplyDoesNotContainAuthenticatedShopperIdAsOrderWasMadeUsingAMerchantToken() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationSerialiserMock.deserialise(orderModificationModelMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getAuthenticatedShopperID()).thenReturn(null);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderNotificationServiceMock).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        verify(businessProcessServiceMock).triggerEvent(anyString());
        verify(worldpayOrderNotificationHandlerMock).setNonDefectiveAndProcessed(orderModificationModelMock);
    }

    @Test
    public void markNotificationAsProcessedWhenContainsTokenReplyContainsNonMatchingAuthenticatedShopperId() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationSerialiserMock.deserialise(orderModificationModelMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getAuthenticatedShopperID()).thenReturn(AUTHENTICATED_SHOPPER_ID);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderNotificationServiceMock).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        verify(businessProcessServiceMock).triggerEvent(anyString());
        verify(worldpayOrderNotificationHandlerMock).setNonDefectiveAndProcessed(orderModificationModelMock);
    }

    @Test
    public void markNotificationAsProcessedWhenDoesNotContainTokenReply() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationSerialiserMock.deserialise(orderModificationModelMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(null);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderNotificationServiceMock).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        verify(businessProcessServiceMock).triggerEvent(anyString());
        verify(worldpayOrderNotificationHandlerMock).setNonDefectiveAndProcessed(orderModificationModelMock);
    }

    @Test
    public void shouldProcessRefundNotification() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, REFUND_FOLLOW_ON, orderModelMock)).thenReturn(Boolean.TRUE);
        when(worldpayOrderModificationRefundProcessStrategyMock.processRefundFollowOn(orderModelMock, orderNotificationMessageMock)).thenReturn(Boolean.TRUE);

        testObj.processOrderModificationMessages(REFUND_FOLLOW_ON);

        verify(orderNotificationServiceMock).processOrderNotificationMessage(orderNotificationMessageMock);
        verify(worldpayOrderNotificationHandlerMock).setNonDefectiveAndProcessed(orderModificationModelMock);
    }

    @Test
    public void shouldMarkRefundNotificationAsDefective() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, REFUND_FOLLOW_ON, orderModelMock)).thenReturn(Boolean.TRUE);
        when(worldpayOrderModificationRefundProcessStrategyMock.processRefundFollowOn(orderModelMock, orderNotificationMessageMock)).thenReturn(Boolean.FALSE);

        testObj.processOrderModificationMessages(REFUND_FOLLOW_ON);

        verify(worldpayOrderNotificationHandlerMock).setDefectiveModification(orderModificationModelMock, null, true);
    }
}
