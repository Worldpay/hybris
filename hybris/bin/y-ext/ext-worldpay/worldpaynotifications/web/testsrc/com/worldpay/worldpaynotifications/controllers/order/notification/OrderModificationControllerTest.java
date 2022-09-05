package com.worldpay.worldpaynotifications.controllers.order.notification;

import com.worldpay.core.event.OrderModificationEvent;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.service.marshalling.impl.DefaultPaymentServiceMarshaller;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.controller.order.notification.OrderModificationController;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static com.worldpay.enums.order.AuthorisedStatus.CAPTURED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderModificationControllerTest {
    private static final String WORLDPAY_RESPONSE_OK_VIEW = "pages/orderNotification/worldpayResponseOkView";

    private static final AuthorisedStatus JOURNAL_TYPE_NOT_HANDLED = AuthorisedStatus.CHARGED_BACK;
    private static final AuthorisedStatus JOURNAL_TYPE_HANDLED = AUTHORISED;

    @InjectMocks
    private OrderModificationController testObj;

    @Mock
    private HttpServletRequest requestMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock(name = "orderNotificationRequestConverter")
    private Converter<PaymentService, OrderNotificationMessage> orderNotificationRequestConverterMock;
    @Mock
    private ServletInputStream requestInputStreamMock;
    @Mock
    private EventService eventServiceMock;
    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private DefaultPaymentServiceMarshaller paymentServiceMarshallerMock;
    @Captor
    private ArgumentCaptor<OrderModificationEvent> orderModificationEventArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        when(paymentServiceMarshallerMock.unmarshal(requestInputStreamMock)).thenReturn(paymentServiceMock);
        final Set<AuthorisedStatus> processableJournalTypeCodes = new HashSet<>();
        processableJournalTypeCodes.add(AUTHORISED);
        processableJournalTypeCodes.add(CAPTURED);
        testObj.setProcessableJournalTypeCodes(processableJournalTypeCodes);

        when(requestMock.getInputStream()).thenReturn(requestInputStreamMock);
    }

    @Test
    public void processOrderNotificationShouldNotProcessTheNotificationIfNotificationNotProcessable() {
        when(orderNotificationRequestConverterMock.convert(paymentServiceMock)).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getJournalReply().getJournalType()).thenReturn(JOURNAL_TYPE_NOT_HANDLED);

        final String result = testObj.processOrderNotification(requestMock);

        assertEquals(WORLDPAY_RESPONSE_OK_VIEW, result);
        verify(eventServiceMock, never()).publishEvent(any(AbstractEvent.class));
    }

    @Test
    public void processOrderNotificationShouldProcessNotificationIfNotificationIsProcessable() {
        when(orderNotificationRequestConverterMock.convert(paymentServiceMock)).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getJournalReply().getJournalType()).thenReturn(JOURNAL_TYPE_HANDLED);

        final String result = testObj.processOrderNotification(requestMock);

        assertEquals(WORLDPAY_RESPONSE_OK_VIEW, result);
        verify(eventServiceMock).publishEvent(orderModificationEventArgumentCaptor.capture());

        final OrderModificationEvent orderModificationEvent = orderModificationEventArgumentCaptor.getValue();
        assertEquals(orderNotificationMessageMock, orderModificationEvent.getOrderNotificationMessage());
    }
}
