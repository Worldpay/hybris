package com.worldpay.notification.processors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.dao.ProcessDefinitionDao;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.worldpaynotifications.enums.DefectiveReason;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static de.hybris.platform.core.enums.OrderStatus.*;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CANCEL;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOrderNotificationHandlerTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String BUSINESS_PROCESS_CODE = "process_code";

    @InjectMocks
    private DefaultWorldpayOrderNotificationHandler testObj;

    @Mock
    private OrderNotificationService orderNodificationServiceMock;
    @Mock
    private ProcessDefinitionDao processDefinitionDaoMock;
    @Mock
    private BusinessProcessService businessProcessServiceMock;

    @Mock
    private WorldpayOrderModificationModel orderModificationModelMock;
    @Mock
    private OrderModel orderMock;
    @Mock
    private OrderNotificationMessage notificationMessageMock;
    @Mock
    private BusinessProcessModel businessProcessMock1, businessProcessMock2;

    private Set<OrderStatus> nonTriggeringOrderStatuses = ImmutableSet.of(CANCELLED, CANCELLING);

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(testObj, "nonTriggeringOrderStatuses", nonTriggeringOrderStatuses);
        when(orderMock.getCode()).thenReturn(ORDER_CODE);
        when(orderMock.getStatus()).thenReturn(CREATED);
        when(processDefinitionDaoMock.findWaitingOrderProcesses(ORDER_CODE, CAPTURE)).thenReturn(ImmutableList.of(businessProcessMock1));
        when(businessProcessMock1.getCode()).thenReturn(BUSINESS_PROCESS_CODE);
        when(orderNodificationServiceMock.isNotificationValid(notificationMessageMock, orderMock)).thenReturn(true);
    }

    @Test
    public void handleNotificationBusinessProcess_WhenValidNotificationValidAndStatusNotInNonTriggeringOrderStatus_ShouldTriggerBusinessProcess() throws WorldpayConfigurationException {
        when(processDefinitionDaoMock.findWaitingOrderProcesses(ORDER_CODE, CAPTURE)).thenReturn(ImmutableList.of(businessProcessMock1));

        testObj.handleNotificationBusinessProcess(CAPTURE, orderModificationModelMock, orderMock, notificationMessageMock);

        verify(orderNodificationServiceMock).processOrderNotificationMessage(notificationMessageMock, orderModificationModelMock);
        verify(orderNodificationServiceMock).setNonDefectiveAndProcessed(orderModificationModelMock);
        verify(businessProcessServiceMock).triggerEvent(BUSINESS_PROCESS_CODE + "_" + CAPTURE);
    }

    @Test
    public void handleNotificationBusinessProcess_WhenValidNotificationValidAndStatusInNonTriggeringOrderStatus_ShouldNotTriggerBusinessProcess() throws WorldpayConfigurationException {
        when(orderMock.getStatus()).thenReturn(CANCELLING);
        when(processDefinitionDaoMock.findWaitingOrderProcesses(ORDER_CODE, CANCEL)).thenReturn(ImmutableList.of(businessProcessMock1));

        testObj.handleNotificationBusinessProcess(CANCEL, orderModificationModelMock, orderMock, notificationMessageMock);

        verify(orderNodificationServiceMock).processOrderNotificationMessage(notificationMessageMock, orderModificationModelMock);
        verify(orderNodificationServiceMock).setNonDefectiveAndProcessed(orderModificationModelMock);
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
    }

    @Test
    public void handleNotificationBusinessProcess_WhenNotificationIsNotValid_ShouldSetDefectiveNotificationAndAdReason() throws WorldpayConfigurationException {
        when(orderNodificationServiceMock.isNotificationValid(notificationMessageMock, orderMock)).thenReturn(false);

        testObj.handleNotificationBusinessProcess(CAPTURE, orderModificationModelMock, orderMock, notificationMessageMock);

        verify(orderNodificationServiceMock).setDefectiveReason(orderModificationModelMock, DefectiveReason.INVALID_AUTHENTICATED_SHOPPER_ID);
        verify(orderNodificationServiceMock).setDefectiveModification(orderModificationModelMock, null, true);
    }

    @Test
    public void handleNotificationBusinessProcess_WhenMoreThanOneBusinessProcessFound_ShouldDoNothing() throws WorldpayConfigurationException {
        when(processDefinitionDaoMock.findWaitingOrderProcesses(ORDER_CODE, CAPTURE)).thenReturn(ImmutableList.of(businessProcessMock1, businessProcessMock2));

        testObj.handleNotificationBusinessProcess(CAPTURE, orderModificationModelMock, orderMock, notificationMessageMock);

        verifyNoInteractions(orderNodificationServiceMock);
        verifyNoInteractions(businessProcessServiceMock);
    }
}
