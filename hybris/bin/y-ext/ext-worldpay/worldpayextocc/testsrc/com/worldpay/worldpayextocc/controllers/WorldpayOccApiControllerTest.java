package com.worldpay.worldpayextocc.controllers;

import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayOccApiControllerTest {

    public static final String CSE_PUBLIC_KEY = "CSE-public-key";

    @InjectMocks
    private WorldpayOccApiController testObj;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;

    @Test
    public void getCsePublicKey_WhenItIsCalled_ReturnsTheCSEPublicKey() {
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData().getCsePublicKey()).thenReturn(CSE_PUBLIC_KEY);

        final String result = testObj.getCsePublicKey();

        assertThat(result).isEqualTo(CSE_PUBLIC_KEY);
    }
}
