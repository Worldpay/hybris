package com.worldpay.service.impl;

import com.worldpay.service.WorldpayAddonEndpointService;
import com.worldpay.service.impl.DefaultWorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayAddonEndpointServiceTest {

    public static final String ADDON_PREFIX = "myAddonPrefix";
    @InjectMocks
    private WorldpayAddonEndpointService testObj = new DefaultWorldpayAddonEndpointService();
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    @Test
    public void shouldReturnPathPrefixedByAddon() throws Exception {
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(DefaultWorldpayAddonEndpointService.WORLDPAY_ADDON_PREFIX, DefaultWorldpayAddonEndpointService.UNDEFINED_PREFIX)).thenReturn(ADDON_PREFIX);

        final String endpoint = testObj.getAutoSubmit3DSecure();

        assertEquals("The endpoint must be prefixed by the addon prefix", ADDON_PREFIX + DefaultWorldpayAddonEndpointService.AUTOSUBMIT3DSECURE, endpoint);
    }
}
