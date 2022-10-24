package com.worldpay.cronjob;

import com.worldpay.strategies.WorldpayOrderModificationNotifierStrategy;
import com.worldpay.worldpaynotifications.model.NotifyUnprocessedOrderModificationsCronJobModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderModificationNotifierJobPerformableTest {
    @InjectMocks
    private OrderModificationNotifierJobPerformable testObj = new OrderModificationNotifierJobPerformable();
    @Mock
    private NotifyUnprocessedOrderModificationsCronJobModel notifyUnprocessedOrderModificationsCronJobModelMock;
    @Mock
    private WorldpayOrderModificationNotifierStrategy worldpayOrderModificationNotifierStrategyMock;

    @Test
    public void performShouldInvokeStrategy() throws Exception {
        testObj.perform(notifyUnprocessedOrderModificationsCronJobModelMock);

        verify(worldpayOrderModificationNotifierStrategyMock).notifyThatOrdersHaveNotBeenProcessed(anyInt());
    }
}
