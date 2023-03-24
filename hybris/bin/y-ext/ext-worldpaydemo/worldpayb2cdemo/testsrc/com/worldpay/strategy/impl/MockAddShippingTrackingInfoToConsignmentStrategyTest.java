package com.worldpay.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MockAddShippingTrackingInfoToConsignmentStrategyTest {

    @InjectMocks
    private MockAddShippingTrackingInfoToConsignmentStrategy testObj;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private ConsignmentModel consignmentModelMock;

    @Test
    public void addTrackingInfo_Consignment_AddTrackingIdAndCurrier() {
        testObj.addTrackingInfo(consignmentModelMock);

        verify(consignmentModelMock).setTrackingID(anyString());
        verify(consignmentModelMock).setCarrier("MockCarrier");
        verify(modelServiceMock).save(consignmentModelMock);
    }
}
