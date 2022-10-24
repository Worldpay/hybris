package com.worldpay.support.appender.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayAddonVersionAppenderTest {

    private static final String ADDON_VERSION = "addonVersion";
    private static final String WORLDPAY_ADDON_VERSION_KEY = "worldpay.addon.version";

    @Mock(answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;

    @InjectMocks
    private WorldpayAddonVersionAppender testObj;

    @Test
    public void appendContent_ShouldAppendAddonVersionContent() {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_ADDON_VERSION_KEY)).thenReturn(ADDON_VERSION);

        final String result = testObj.appendContent();

        assertTrue(result.contains("Worldpay Plugin Version: " + ADDON_VERSION));
    }

    @Test
    public void appendContent_ShouldReturnEmptyContentToAppend_WhenAddonVersionIsUnknown() {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_ADDON_VERSION_KEY)).thenReturn(EMPTY);

        final String result = testObj.appendContent();

        assertTrue(result.isEmpty());
    }
}
