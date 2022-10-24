package com.worldpay.strategies.impl;

import com.worldpay.dao.OrderModificationDao;
import com.worldpay.worldpaynotifications.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayOrderModificationCleanUpStrategyTest {

    @InjectMocks
    private DefaultWorldpayOrderModificationCleanUpStrategy testObj;

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayOrderModificationModel orderModificationMock;
    @Mock
    private OrderModificationDao orderModificationDaoMock;

    @Test
    public void removeProcessOrderModificationsIfAnyAreFound() {
        when(orderModificationDaoMock.findProcessedOrderModificationsBeforeDate(any(Date.class))).thenReturn(Collections.singletonList(orderModificationMock));

        testObj.doCleanUp(5);

        verify(modelServiceMock).remove(orderModificationMock);
    }

    @Test
    public void doNothingIfNoProcessOrderModificationsAreFound() {
        when(orderModificationDaoMock.findProcessedOrderModificationsBeforeDate(any(Date.class))).thenReturn(Collections.emptyList());

        testObj.doCleanUp(5);

        verify(modelServiceMock, never()).remove(any(WorldpayOrderModificationModel.class));
    }
}
