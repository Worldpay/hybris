package com.worldpay.core.services.impl;

import com.google.common.collect.ImmutableMap;
import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.dao.OrderModificationDao;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static com.worldpay.enums.order.AuthorisedStatus.REFUSED;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderNotificationServiceTest {

    private static final String VALID_AUTHENTICATED_SHOPPER_ID = "validAuthenticatedShopperId";
    private static final String ORDER_CODE = "orderCode";

    @Spy
    @InjectMocks
    private DefaultOrderNotificationService testObj;

    @Mock
    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDaoMock;
    @Mock
    private OrderNotificationProcessorStrategy orderNotificationProcessorStrategyMock;
    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private OrderModificationDao orderModificationDaoMock;
    @Mock
    private ModelService modelServiceMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private WorldpayOrderModificationModel worldpayOrderModificationMock,existingModificationModelMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private AbstractOrderModel orderMock;
    @Mock
    private Exception exceptionMock;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(testObj, "journalTypeToNotificationProcessorStrategyMap", ImmutableMap.of(AUTHORISED, orderNotificationProcessorStrategyMock));

        when(worldpayCartServiceMock.getAuthenticatedShopperId(orderMock)).thenReturn(VALID_AUTHENTICATED_SHOPPER_ID);
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

    @Test
    public void getUnprocessedOrderModificationsByType_ShouldCallDao() {
        testObj.getUnprocessedOrderModificationsByType(PaymentTransactionType.CAPTURE);

        verify(orderModificationDaoMock).findUnprocessedOrderModificationsByType(PaymentTransactionType.CAPTURE);
    }

    @Test
    public void getExistingModifications_ShouldCallDao() {
        testObj.getExistingModifications(worldpayOrderModificationMock);

        verify(orderModificationDaoMock).findExistingModifications(worldpayOrderModificationMock);
    }

    @Test
    public void setDefectiveModification_ShouldSetDefectiveAndProcessed() {
        testObj.setDefectiveModification(worldpayOrderModificationMock, exceptionMock, true);

        verify(worldpayOrderModificationMock).setDefective(Boolean.TRUE);
        verify(worldpayOrderModificationMock).setProcessed(true);
        verify(modelServiceMock).save(worldpayOrderModificationMock);
    }

    @Test
    public void setDefectiveReason_WhenDefectiveCounterIsNotNull_ShouldSetDefectiveReasonAndIncreaseCounter() {
        doReturn(singletonList(existingModificationModelMock)).when(testObj).getExistingModifications(worldpayOrderModificationMock);
        when(existingModificationModelMock.getDefectiveCounter()).thenReturn(1);

        testObj.setDefectiveReason(worldpayOrderModificationMock, DefectiveReason.PROCESSING_ERROR);

        verify(worldpayOrderModificationMock).setDefectiveReason(DefectiveReason.PROCESSING_ERROR);
        verify(worldpayOrderModificationMock).setDefectiveCounter(2);
        verify(modelServiceMock).remove(existingModificationModelMock);
    }

    @Test
    public void setDefectiveReason_WhenDefectiveCounterIsNull_ShouldSetDefectiveReasonAndSetCounterTo1() {
        doReturn(singletonList(worldpayOrderModificationMock)).when(testObj).getExistingModifications(worldpayOrderModificationMock);
        when(worldpayOrderModificationMock.getDefectiveCounter()).thenReturn(null);

        testObj.setDefectiveReason(worldpayOrderModificationMock, DefectiveReason.PROCESSING_ERROR);

        verify(worldpayOrderModificationMock).setDefectiveReason(DefectiveReason.PROCESSING_ERROR);
        verify(worldpayOrderModificationMock).setDefectiveCounter(1);
        verify(modelServiceMock).remove(worldpayOrderModificationMock);
    }

    @Test
    public void setNonDefectiveAndProcessed() {
        testObj.setNonDefectiveAndProcessed(worldpayOrderModificationMock);

        verify(worldpayOrderModificationMock).setProcessed(Boolean.TRUE);
        verify(worldpayOrderModificationMock).setDefective(Boolean.FALSE);
        verify(modelServiceMock).save(worldpayOrderModificationMock);
    }

}
