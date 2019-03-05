package com.worldpay.controllers.cms;

import com.worldpay.config.merchant.ApplePayConfigData;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import de.hybris.platform.addonsupport.controllers.cms.GenericCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller("WorldpayApplePayComponentController")
@RequestMapping(value = "/view/WorldpayApplePayComponentController")
public class WorldpayApplePayComponentController extends GenericCMSAddOnComponentController {
    protected static final String APPLE_PAY_CONFIG = "applePaySettings";

    @Resource
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component) {
        invokeSuperFillModel(request, model, component);

        model.addAttribute(APPLE_PAY_CONFIG, getGooglePaySettings());
    }

    protected ApplePayConfigData getGooglePaySettings() {
        final WorldpayMerchantConfigData config = worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData();

        return config.getApplePaySettings();
    }

    protected void invokeSuperFillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component) {
        super.fillModel(request, model, component);
    }
}
