package com.worldpay.orderprocess.actions.order;

import com.worldpay.orderprocess.strategies.cancel.WorldpayCancelWholeOrderDueToCancelNotificationStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CancelWholeOrderDueToCancelNotificationActionTest {

    @InjectMocks
    private CancelWholeOrderDueToCancelNotificationAction testObj;
    @Mock
    private WorldpayCancelWholeOrderDueToCancelNotificationStrategy worldpayCancelWholeOrderDueToCancelNotificationStrategyMock;
    @Mock
    private OrderProcessModel orderProcessModelMock;

    @Test
    public void shouldCancelTheOrder() {
        testObj.executeAction(orderProcessModelMock);

        verify(worldpayCancelWholeOrderDueToCancelNotificationStrategyMock).cancelOrder(orderProcessModelMock);
    }

}
