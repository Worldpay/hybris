package com.worldpay.strategy.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.merchant.WorldpayMerchantConfigDataService;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static com.worldpay.strategy.WorldpayMerchantStrategy.*;
import static de.hybris.platform.commerceservices.enums.UiExperienceLevel.DESKTOP;
import static de.hybris.platform.commerceservices.enums.UiExperienceLevel.MOBILE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayMerchantStrategyTest {

    @InjectMocks
    private DefaultWorldpayMerchantStrategy testObj;

    @Mock
    private WorldpayMerchantConfigDataService worldpayMerchantConfigDataServiceMock;
    @Mock
    private WorldpayMerchantConfigData websiteMerchantConfigDataMock, customerServiceMerchantConfigDataMock, replenishmentMerchantConfigDataMock;
    @Mock
    private Map<String, WorldpayMerchantConfigData> merchantConfigDataMapMock;

    @Before
    public void setUp() throws Exception {
        when(worldpayMerchantConfigDataServiceMock.getMerchantConfiguration()).thenReturn(merchantConfigDataMapMock);
        when(merchantConfigDataMapMock.get(DESKTOP_MERCHANT)).thenReturn(websiteMerchantConfigDataMock);
        when(merchantConfigDataMapMock.get(CUSTOMER_SERVICE_MERCHANT)).thenReturn(customerServiceMerchantConfigDataMock);
        when(merchantConfigDataMapMock.get(REPLENISHMENT_MERCHANT)).thenReturn(replenishmentMerchantConfigDataMock);
    }

    @Test
    public void shouldReturnWebMerchantWhenUIExperienceIsDesktop() throws Exception {

        final WorldpayMerchantConfigData result = testObj.getMerchant(DESKTOP);

        assertEquals(websiteMerchantConfigDataMock, result);
    }

    @Test
    public void shouldReturnWebSiteMerchantConfigurationWhenUiExperienceIsNull() throws Exception {
        final WorldpayMerchantConfigData result = testObj.getMerchant(null);

        assertEquals(websiteMerchantConfigDataMock, result);
    }

    @Test
    public void shouldReturnCustomerServiceMerchant() throws Exception {

        final WorldpayMerchantConfigData result = testObj.getCustomerServiceMerchant();

        assertEquals(customerServiceMerchantConfigDataMock, result);
    }

    @Test
    public void shouldReturnReplenishmentMerchant() throws Exception {

        final WorldpayMerchantConfigData result = testObj.getReplenishmentMerchant();

        assertEquals(replenishmentMerchantConfigDataMock, result);
    }
}
