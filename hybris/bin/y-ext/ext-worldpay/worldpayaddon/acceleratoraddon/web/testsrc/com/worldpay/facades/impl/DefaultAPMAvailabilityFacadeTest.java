package com.worldpay.facades.impl;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.APMAvailabilityService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAPMAvailabilityFacadeTest {

    @InjectMocks
    private DefaultAPMAvailabilityFacade testObj;

    @Mock
    private APMAvailabilityService apmAvailabilityServiceMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationLookupServiceMock;
    @Mock
    private CartService cartServiceMock;

    @Mock
    private WorldpayAPMConfigurationModel apmConfigurationMock;
    @Mock
    private CartModel cartModelMock;

    @Test
    public void isAvailableInvokesApmAvailabilityService() {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(apmAvailabilityServiceMock.isAvailable(apmConfigurationMock, cartModelMock)).thenReturn(true);

        final boolean result = testObj.isAvailable(apmConfigurationMock);

        assertTrue(result);
        verify(apmAvailabilityServiceMock).isAvailable(apmConfigurationMock, cartModelMock);
    }

}
