package com.worldpay.facades.payment.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayMerchantConfigDataFacadeTest {

    @InjectMocks
    private DefaultWorldpayMerchantConfigDataFacade testObj;
    @Mock
    private WorldpayMerchantConfigurationModel worldpayMerchantConfigMock;
    @Mock
    private WorldpayMerchantStrategy worldpayMerchantStrategyMock;
    @Mock
    private Converter<WorldpayMerchantConfigurationModel, WorldpayMerchantConfigData> worldPayMerchantConfigDataConverter;
    @Mock
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;

    @Before
    public void setUp() {
        when(worldpayMerchantStrategyMock.getMerchant()).thenReturn(worldpayMerchantConfigMock);
        when(worldPayMerchantConfigDataConverter.convert(worldpayMerchantConfigMock)).thenReturn(worldpayMerchantConfigDataMock);
    }

    @Test
    public void shouldReturnWebSiteMerchantConfigData() {

        final WorldpayMerchantConfigData result = testObj.getCurrentSiteMerchantConfigData();

        assertEquals(worldpayMerchantConfigDataMock, result);
    }
}
