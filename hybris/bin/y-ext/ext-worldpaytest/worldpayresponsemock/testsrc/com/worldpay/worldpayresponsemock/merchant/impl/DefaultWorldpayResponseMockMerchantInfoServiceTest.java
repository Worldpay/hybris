package com.worldpay.worldpayresponsemock.merchant.impl;

import com.worldpay.merchant.configuration.services.WorldpayMerchantConfigurationService;
import com.worldpay.model.WorldpayMerchantConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayResponseMockMerchantInfoServiceTest {

    private static final String MERCHANT_CODE = "merchantCode";

    @InjectMocks
    private DefaultWorldpayResponseMockMerchantInfoService testObj;

    @Mock
    private WorldpayMerchantConfigurationService worldpayMerchantConfigurationServiceMock;
    @Mock
    private WorldpayMerchantConfigurationModel webWorldpayMerchantConfigurationMock, asmWorldpayMerchantConfigurationMock;

    @Test
    public void getAllMerchantCodes_ShouldReturnUniqueMerchantCodes() {
        when(worldpayMerchantConfigurationServiceMock.getAllSystemActiveSiteMerchantConfigurations()).thenReturn(Set.of(webWorldpayMerchantConfigurationMock, asmWorldpayMerchantConfigurationMock));
        when(webWorldpayMerchantConfigurationMock.getCode()).thenReturn(MERCHANT_CODE);
        when(asmWorldpayMerchantConfigurationMock.getCode()).thenReturn(MERCHANT_CODE);

        final Set<String> allMerchantCodes = testObj.getAllMerchantCodes();

        assertThat(allMerchantCodes.size()).isEqualTo(1);
        assertThat(allMerchantCodes.iterator().next()).isEqualTo(MERCHANT_CODE);
    }

}
