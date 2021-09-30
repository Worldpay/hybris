package com.worldpay.voidprocess.listener;

import com.google.common.collect.ImmutableList;
import com.worldpay.dao.ProcessDefinitionDao;
import com.worldpay.voidprocess.model.WorldpayVoidProcessModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.events.CancelFinishedEvent;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.voidprocess.listener.WorldpayCancelFinishedEventListener.WORLDPAY_VOID_PROCESS_NAME;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCancelFinishedEventListenerTest {

    private static final String ORDER_BUSINESS_PROCESS_CODE = "businessProcessCode";
    private static final String ORDER_CODE = "orderCode";
    private static final long CURRENT_TIME_IN_MILLIS = 12l;
    private static final String VOID_BUSINESS_PROCESS_CODE = WORLDPAY_VOID_PROCESS_NAME + "-" + ORDER_CODE + "-" + CURRENT_TIME_IN_MILLIS;

    @Spy
    @InjectMocks
    private WorldpayCancelFinishedEventListener testObj;

    @Mock
    private BusinessProcessService businessProcessServiceMock;
    @Mock
    private ProcessDefinitionDao processDefinitionDaoMock;
    @Mock
    private ModelService modelServiceMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CancelFinishedEvent cancelFinishedEventMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private WorldpayVoidProcessModel worldpayVoidProcessModelMock;
    @Mock
    private BusinessProcessModel businessProcessMock;

    @Before
    public void setUp() {
        when(orderModelMock.getCode()).thenReturn(ORDER_CODE);
        when(processDefinitionDaoMock.findWaitingOrderProcesses(ORDER_CODE, PaymentTransactionType.CANCEL)).thenReturn(ImmutableList.of(businessProcessMock));
        when(businessProcessMock.getCode()).thenReturn(ORDER_BUSINESS_PROCESS_CODE);

        when(cancelFinishedEventMock.getCancelRequestRecordEntry().getModificationRecord().getOrder()).thenReturn(orderModelMock);
        when(businessProcessServiceMock.createProcess(VOID_BUSINESS_PROCESS_CODE, WORLDPAY_VOID_PROCESS_NAME)).thenReturn(worldpayVoidProcessModelMock);
        doReturn(CURRENT_TIME_IN_MILLIS).when(testObj).getCurrentTimeInMillis();
    }

    @Test
    public void onEvent_ShouldCreateNewWorldpayVoidProcessAndTriggerCancelEvent() {
        testObj.onEvent(cancelFinishedEventMock);

        verify(businessProcessServiceMock).createProcess(VOID_BUSINESS_PROCESS_CODE, WORLDPAY_VOID_PROCESS_NAME);
        verify(worldpayVoidProcessModelMock).setOrder(orderModelMock);
        verify(modelServiceMock).save(worldpayVoidProcessModelMock);
        verify(businessProcessServiceMock).startProcess(worldpayVoidProcessModelMock);
        verify(businessProcessServiceMock).triggerEvent(ORDER_BUSINESS_PROCESS_CODE + "_" + PaymentTransactionType.CANCEL);
    }

    @Test
    public void onEvent_WhenNoOrderProcessFound_ShouldNotTriggerEvent() {
        when(processDefinitionDaoMock.findWaitingOrderProcesses(ORDER_CODE, PaymentTransactionType.CANCEL)).thenReturn(ImmutableList.of());

        testObj.onEvent(cancelFinishedEventMock);

        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
    }
}
