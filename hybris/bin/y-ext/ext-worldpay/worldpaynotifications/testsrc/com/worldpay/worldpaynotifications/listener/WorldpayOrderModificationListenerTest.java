package com.worldpay.worldpaynotifications.listener;

import com.worldpay.core.event.OrderModificationEvent;
import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.data.JournalReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static com.worldpay.enums.order.AuthorisedStatus.REFUSED;
import static com.worldpay.enums.order.AuthorisedStatus.*;
import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayOrderModificationListenerTest {

    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String ORDER_CODE = "orderCode";
    private static final String SERIALIZED = "serialized";
    private static final String RETURN_CODE = "A19";

    @InjectMocks
    private WorldpayOrderModificationListener testObj;

    @Mock
    private OrderModificationEvent orderModificationEventMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private JournalReply journalReplyMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderNotificationService orderNotificationServiceMock;
    @Mock
    private WorldpayOrderModificationModel worldpayOrderModificationModelMock;
    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private CartModel cartModelMock;

    private Map<AuthorisedStatus, PaymentTransactionType> paymentTransactionTypeMap = new HashMap<>();

    @Before
    public void setup() {
        ReflectionTestUtils.setField(testObj, "paymentTransactionTypeMap", paymentTransactionTypeMap);
        paymentTransactionTypeMap.put(AUTHORISED, AUTHORIZATION);
        paymentTransactionTypeMap.put(REFUSED, PaymentTransactionType.REFUSED);
        paymentTransactionTypeMap.put(CANCELLED, CANCEL);
        paymentTransactionTypeMap.put(CAPTURED, CAPTURE);
        when(orderModificationEventMock.getOrderNotificationMessage()).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getPaymentReply().getReturnCode()).thenReturn(RETURN_CODE);
        when(orderNotificationMessageMock.getJournalReply()).thenReturn(journalReplyMock);
        when(orderNotificationMessageMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderNotificationServiceMock.serialiseNotification(orderNotificationMessageMock)).thenReturn(SERIALIZED);
        when(modelServiceMock.create(WorldpayOrderModificationModel.class)).thenReturn(worldpayOrderModificationModelMock);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
    }

    @Test
    public void onEventRefusedShouldUpdateCartModelWhenDeclineCodeExists() {
        when(journalReplyMock.getJournalType()).thenReturn(REFUSED);

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock).setWorldpayDeclineCodeOnCart(WORLDPAY_ORDER_CODE, RETURN_CODE);
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
    }

    @Test
    public void onEventRefusedShouldNotUpdateCartModelWhenDeclineCodeIsZero() {
        when(journalReplyMock.getJournalType()).thenReturn(REFUSED);
        when(orderNotificationMessageMock.getPaymentReply().getReturnCode()).thenReturn("0");

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock, never()).setWorldpayDeclineCodeOnCart(anyString(), anyString());
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
        verify(worldpayOrderModificationModelMock).setType(PaymentTransactionType.REFUSED);
        verify(worldpayOrderModificationModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(worldpayOrderModificationModelMock).setOrderNotificationMessage(SERIALIZED);
    }

    @Test
    public void onEventAUTHORISEDShouldSaveModificationModel() {
        when(journalReplyMock.getJournalType()).thenReturn(AUTHORISED);

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock, never()).setWorldpayDeclineCodeOnCart(anyString(), anyString());
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
        verify(worldpayOrderModificationModelMock).setType(AUTHORIZATION);
        verify(worldpayOrderModificationModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(worldpayOrderModificationModelMock).setOrderNotificationMessage(SERIALIZED);
    }

    @Test
    public void onEventCAPTUREDShouldSaveModificationModel() {
        when(journalReplyMock.getJournalType()).thenReturn(CAPTURED);

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock, never()).setWorldpayDeclineCodeOnCart(anyString(), anyString());
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
        verify(worldpayOrderModificationModelMock).setType(CAPTURE);
        verify(worldpayOrderModificationModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(worldpayOrderModificationModelMock).setOrderNotificationMessage(SERIALIZED);
    }

    @Test
    public void onEventREFUSEDShouldSaveModificationModelWhenTransactionBelongsToACart() {
        when(journalReplyMock.getJournalType()).thenReturn(REFUSED);
        when(orderNotificationMessageMock.getPaymentReply().getReturnCode()).thenReturn("0");

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock, never()).setWorldpayDeclineCodeOnCart(anyString(), anyString());
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
        verify(worldpayOrderModificationModelMock).setType(any(PaymentTransactionType.class));
        verify(worldpayOrderModificationModelMock).setWorldpayOrderCode(anyString());
        verify(worldpayOrderModificationModelMock).setOrderNotificationMessage(anyString());
    }


    @Test
    public void onEventREFUSEDShouldSaveModificationModelWhenTransactionBelongsToAnOrder() {
        when(journalReplyMock.getJournalType()).thenReturn(REFUSED);
        when(orderNotificationMessageMock.getPaymentReply().getReturnCode()).thenReturn("0");

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock, never()).setWorldpayDeclineCodeOnCart(anyString(), anyString());
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
        verify(worldpayOrderModificationModelMock).setType(PaymentTransactionType.REFUSED);
        verify(worldpayOrderModificationModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(worldpayOrderModificationModelMock).setOrderNotificationMessage(SERIALIZED);
    }

    @Test
    public void onEventCANCELLEDShouldSaveModificationModelWhenTransactionBelongsToACart() {
        when(journalReplyMock.getJournalType()).thenReturn(CANCELLED);

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock, never()).setWorldpayDeclineCodeOnCart(anyString(), anyString());
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
        verify(worldpayOrderModificationModelMock).setType(CANCEL);
        verify(worldpayOrderModificationModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(worldpayOrderModificationModelMock).setOrderNotificationMessage(SERIALIZED);
    }

    @Test
    public void onEventCANCELLEDShouldSaveModificationModelWhenTransactionBelongsToAnOrder() {
        when(journalReplyMock.getJournalType()).thenReturn(CANCELLED);

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock, never()).setWorldpayDeclineCodeOnCart(anyString(), anyString());
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
        verify(worldpayOrderModificationModelMock).setType(CANCEL);
        verify(worldpayOrderModificationModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(worldpayOrderModificationModelMock).setOrderNotificationMessage(SERIALIZED);
    }

    @Test
    public void onEventShouldNotSaveModificationModelDueToIncorrectMapping() {
        when(journalReplyMock.getJournalType()).thenReturn(EXPIRED);

        testObj.onEvent(orderModificationEventMock);

        verify(modelServiceMock, never()).save(any());
    }
}
