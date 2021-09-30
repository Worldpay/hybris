package com.worldpay.merchant.configuration.services.impl;

import com.worldpay.model.WorldpayMerchantConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayMerchantConfigurationServiceTest {

    private static final String BASE_SITE_ID = "base-site-id";

    @InjectMocks
    private DefaultWorldpayMerchantConfigurationService testObj;

    @Mock
    private BaseSiteService baseSiteServiceMock;
    @Mock
    private CMSSiteModel siteMock1, siteMock2, siteMock3;
    @Mock
    private WorldpayMerchantConfigurationModel webMerchantConfigurationMock, asmMerchantConfigurationMock, replenishmentMerchantConfigurationMock;

    @Before
    public void setUp() {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(siteMock1);
        when(baseSiteServiceMock.getBaseSiteForUID(BASE_SITE_ID)).thenReturn(siteMock1);
    }

    @Test
    public void getCurrentWebConfiguration_ShouldReturnTheValueAsExpected() {
        when(siteMock1.getWebMerchantConfiguration()).thenReturn(webMerchantConfigurationMock);

        final WorldpayMerchantConfigurationModel result = testObj.getCurrentWebConfiguration();

        assertSame(webMerchantConfigurationMock, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCurrentWebConfiguration_WhenSiteNull_ShouldThrowException() {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(null);

        testObj.getCurrentWebConfiguration();
    }

    @Test
    public void getCurrentAsmConfiguration_ShouldReturnTheValueAsExpected() {
        when(siteMock1.getAsmMerchantConfiguration()).thenReturn(asmMerchantConfigurationMock);

        final WorldpayMerchantConfigurationModel result = testObj.getCurrentAsmConfiguration();

        assertSame(asmMerchantConfigurationMock, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCurrentAsmConfiguration_WhenSiteNull_ShouldThrowException() {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(null);

        testObj.getCurrentAsmConfiguration();
    }

    @Test
    public void getAllCurrentSiteMerchantConfigurations_ShouldReturnAllTheCurrentSiteMerchantConfig() {
        when(siteMock1.getWebMerchantConfiguration()).thenReturn(webMerchantConfigurationMock);
        when(siteMock1.getAsmMerchantConfiguration()).thenReturn(asmMerchantConfigurationMock);
        when(siteMock1.getReplenishmentMerchantConfiguration()).thenReturn(replenishmentMerchantConfigurationMock);

        final Set<WorldpayMerchantConfigurationModel> result = testObj.getAllCurrentSiteMerchantConfigurations();

        assertEquals(3, result.size());
        assertTrue(result.contains(webMerchantConfigurationMock));
        assertTrue(result.contains(asmMerchantConfigurationMock));
        assertTrue(result.contains(replenishmentMerchantConfigurationMock));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAllCurrentSiteMerchantConfigurations_WhenSiteNull_ShouldThrowException() {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(null);

        testObj.getAllCurrentSiteMerchantConfigurations();
    }

    @Test
    public void getAllSystemActiveSiteMerchantConfigurations_ShouldGetAllTheSystemMerchantConfigForActiveSites() {
        when(baseSiteServiceMock.getAllBaseSites()).thenReturn(asList(siteMock1, siteMock2, siteMock3));
        when(siteMock1.getWebMerchantConfiguration()).thenReturn(webMerchantConfigurationMock);
        when(siteMock1.getAsmMerchantConfiguration()).thenReturn(asmMerchantConfigurationMock);
        when(siteMock1.getActive()).thenReturn(true);
        when(siteMock2.getAsmMerchantConfiguration()).thenReturn(null);
        when(siteMock2.getActive()).thenReturn(true);
        when(siteMock3.getReplenishmentMerchantConfiguration()).thenReturn(replenishmentMerchantConfigurationMock);
        when(siteMock3.getActive()).thenReturn(false);

        final Set<WorldpayMerchantConfigurationModel> result = testObj.getAllSystemActiveSiteMerchantConfigurations();

        assertEquals(2, result.size());
        assertTrue(result.contains(webMerchantConfigurationMock));
        assertTrue(result.contains(asmMerchantConfigurationMock));
    }
}
