package com.worldpay.consignmentprocess.actions.consigment;

import com.worldpay.consignmentprocess.strategies.AddShippingTrackingInfoToConsignmentStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AddShippingTrackingInfoActionTest {

    @InjectMocks
    private AddShippingTrackingInfoAction testObj;

    @Mock
    private AddShippingTrackingInfoToConsignmentStrategy addShippingTrackingInfoToConsignmentStrategyMock;
    @Mock
    private ConsignmentProcessModel consignmentProcessModelMock;
    @Mock
    private ConsignmentModel consignmentModelMock;

    @Test
    public void executeAction_ShouldCallAddShippingTrackingInfoToConsignmentStrategy() throws Exception {
        when(consignmentProcessModelMock.getConsignment()).thenReturn(consignmentModelMock);

        testObj.executeAction(consignmentProcessModelMock);

        verify(addShippingTrackingInfoToConsignmentStrategyMock).addTrackingInfo(consignmentModelMock);
    }
}
