package com.worldpay.service.apm.impl;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.strategy.APMAvailabilityStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAPMAvailabilityServiceTest {

    @InjectMocks
    private DefaultAPMAvailabilityService testObj;

    @Mock
    private APMAvailabilityStrategy apmAvailabilityStrategy1ReturnsTrueMock, apmAvailabilityStrategy2ReturnsTrueMock, apmAvailabilityStrategyReturnsFalseMock;
    @Mock
    private WorldpayAPMConfigurationModel worldpayAPMConfigurationModelMock;
    @Mock
    private CartModel cartModelMock;

    @Before
    public void setUp() {
        when(apmAvailabilityStrategy1ReturnsTrueMock.isAvailable(worldpayAPMConfigurationModelMock, cartModelMock)).thenReturn(true);
        when(apmAvailabilityStrategy2ReturnsTrueMock.isAvailable(worldpayAPMConfigurationModelMock, cartModelMock)).thenReturn(true);
        when(apmAvailabilityStrategyReturnsFalseMock.isAvailable(worldpayAPMConfigurationModelMock, cartModelMock)).thenReturn(false);
    }

    @Test
    public void isAvailableReturnsTrueIfAllStrategiesReturnTrue() {
        testObj.setApmAvailabilityStrategyList(Arrays.asList(apmAvailabilityStrategy1ReturnsTrueMock, apmAvailabilityStrategy2ReturnsTrueMock));

        final boolean result = testObj.isAvailable(worldpayAPMConfigurationModelMock, cartModelMock);

        assertTrue(result);
    }

    @Test
    public void isAvailableReturnsFalseIfAnyStrategyReturnsFalse() {
        testObj.setApmAvailabilityStrategyList(Arrays.asList(apmAvailabilityStrategy1ReturnsTrueMock, apmAvailabilityStrategyReturnsFalseMock, apmAvailabilityStrategy2ReturnsTrueMock));

        final boolean result = testObj.isAvailable(worldpayAPMConfigurationModelMock, cartModelMock);

        assertFalse(result);

        verify(apmAvailabilityStrategy1ReturnsTrueMock).isAvailable(worldpayAPMConfigurationModelMock, cartModelMock);
        verify(apmAvailabilityStrategyReturnsFalseMock).isAvailable(worldpayAPMConfigurationModelMock, cartModelMock);
        verify(apmAvailabilityStrategy2ReturnsTrueMock, never()).isAvailable(worldpayAPMConfigurationModelMock, cartModelMock);
    }

}
