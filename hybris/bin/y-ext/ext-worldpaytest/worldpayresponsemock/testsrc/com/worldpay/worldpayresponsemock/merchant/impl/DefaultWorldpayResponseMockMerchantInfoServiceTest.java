package com.worldpay.worldpayresponsemock.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayResponseMockMerchantInfoServiceTest {

    private static final String MERCHANT_CODE = "merchantCode";

    @InjectMocks
    private DefaultWorldpayResponseMockMerchantInfoService testObj;

    @Mock
    private WorldpayMerchantConfigData worldpayMerchantConfigData;

    @Test
    public void getAllMerchantCodes() {
        testObj.setConfiguredMerchants(singletonList(worldpayMerchantConfigData));
        when(worldpayMerchantConfigData.getCode()).thenReturn(MERCHANT_CODE);

        final Set<String> allMerchantCodes = testObj.getAllMerchantCodes();

        assertThat(allMerchantCodes.size()).isEqualTo(1);
        assertThat(allMerchantCodes.iterator().next()).isEqualTo(MERCHANT_CODE);

    }

}
