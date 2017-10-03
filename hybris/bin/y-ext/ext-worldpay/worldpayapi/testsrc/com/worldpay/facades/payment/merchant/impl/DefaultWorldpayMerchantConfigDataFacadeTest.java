package com.worldpay.facades.payment.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayMerchantConfigDataFacadeTest {

    @InjectMocks
    private DefaultWorldpayMerchantConfigDataFacade testObj;
    @Mock
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;
    @Mock
    private WorldpayMerchantStrategy worldpayMerchantStrategyMock;

    @Before
    public void setup() {
        when(worldpayMerchantStrategyMock.getMerchant()).thenReturn(worldpayMerchantConfigDataMock);
    }

    @Test
    public void shouldReturnWebSiteMerchantConfigData() throws Exception {

        final WorldpayMerchantConfigData result = testObj.getCurrentSiteMerchantConfigData();

        assertEquals(worldpayMerchantConfigDataMock, result);
    }
}
