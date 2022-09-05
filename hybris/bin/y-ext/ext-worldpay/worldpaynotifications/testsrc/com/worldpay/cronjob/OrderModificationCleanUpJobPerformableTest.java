package com.worldpay.cronjob;

import com.worldpay.strategies.WorldpayOrderModificationCleanUpStrategy;
import com.worldpay.worldpaynotifications.model.CleanUpProcessedOrderModificationsCronJobModel;
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
public class OrderModificationCleanUpJobPerformableTest {

    @InjectMocks
    private OrderModificationCleanUpJobPerformable testObj = new OrderModificationCleanUpJobPerformable();
    @Mock
    private WorldpayOrderModificationCleanUpStrategy worldpayOrderModificationCleanUpStrategy;
    @Mock
    private CleanUpProcessedOrderModificationsCronJobModel cleanUpProcessedOrderModificationsCronJobModelMock;

    @Test
    public void performShouldRunTheImplementedStrategyJob() {
        testObj.perform(cleanUpProcessedOrderModificationsCronJobModelMock);

        verify(worldpayOrderModificationCleanUpStrategy).doCleanUp(anyInt());
    }
}
