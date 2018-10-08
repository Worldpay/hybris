package com.worldpay.service.impl;

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

    private static final String ADDON_PREFIX = "myAddonPrefix";
    private static final String WORLDPAY_ADDON_PREFIX = "worldpay.addon.prefix";
    private static final String UNDEFINED_PREFIX = "undefined";
    private static final String AUTOSUBMIT3DSECURE = "pages/checkout/multi/autoSubmit3DSecure";

    @InjectMocks
    private DefaultWorldpayAddonEndpointService testObj;

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    @Test
    public void shouldReturnPathPrefixedByAddon() {
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(WORLDPAY_ADDON_PREFIX, UNDEFINED_PREFIX)).thenReturn(ADDON_PREFIX);

        final String endpoint = testObj.getAutoSubmit3DSecure();

        assertEquals("The endpoint must be prefixed by the addon prefix", ADDON_PREFIX + AUTOSUBMIT3DSECURE, endpoint);
    }
}
