package com.worldpay.controllers.cms;

import com.worldpay.config.merchant.ApplePayConfigData;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayApplePayComponentControllerTest {

    @Spy
    @InjectMocks
    private WorldpayApplePayComponentController testObj;

    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;

    @Mock
    private Model model;

    @Test
    public void testSettingsArePopulated() {
        final WorldpayMerchantConfigData config = new WorldpayMerchantConfigData();
        final ApplePayConfigData applePaySettings = mock(ApplePayConfigData.class);
        config.setApplePaySettings(applePaySettings);
        config.setCode("Merchant");

        when(worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData()).thenReturn(config);
        doNothing().when(testObj).invokeSuperFillModel(any(), any(), any());

        testObj.fillModel(null, model, null);

        verify(worldpayMerchantConfigDataFacade).getCurrentSiteMerchantConfigData();
        verify(model).addAttribute("applePaySettings", applePaySettings);
    }

}
