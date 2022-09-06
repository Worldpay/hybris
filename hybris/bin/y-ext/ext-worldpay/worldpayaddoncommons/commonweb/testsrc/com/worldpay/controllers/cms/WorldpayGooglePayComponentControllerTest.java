package com.worldpay.controllers.cms;

import com.worldpay.config.merchant.GooglePayConfigData;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayGooglePayComponentControllerTest {

    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;

    @Mock
    private Model model;

    @Spy
    @InjectMocks
    private WorldpayGooglePayComponentController testObj;

    @Test
    public void testSettingsArePopulated() {
        final WorldpayMerchantConfigData config = new WorldpayMerchantConfigData();
        final GooglePayConfigData googlePaySettings = mock(GooglePayConfigData.class);
        config.setGooglePaySettings(googlePaySettings);
        config.setCode("Merchant");

        when(worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData()).thenReturn(config);
        doNothing().when(testObj).invokeSuperFillModel(any(), any(), any());

        testObj.fillModel(null, model, null);

        verify(worldpayMerchantConfigDataFacade).getCurrentSiteMerchantConfigData();
        verify(model).addAttribute("googlePaySettings", googlePaySettings);
    }
}
