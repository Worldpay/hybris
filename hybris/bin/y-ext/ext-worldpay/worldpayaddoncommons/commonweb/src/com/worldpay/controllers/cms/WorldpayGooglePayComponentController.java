package com.worldpay.controllers.cms;

import com.worldpay.config.merchant.GooglePayConfigData;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import de.hybris.platform.addonsupport.controllers.cms.GenericCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller("WorldpayGooglePayComponentController")
@RequestMapping(value = "/view/WorldpayGooglePayComponentController")
public class WorldpayGooglePayComponentController extends GenericCMSAddOnComponentController {
    protected static final String GOOGLE_PAY_CONFIG = "googlePaySettings";

    @Resource
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component) {
        invokeSuperFillModel(request, model, component);

        model.addAttribute(GOOGLE_PAY_CONFIG, getGooglePaySettings());
    }

    protected GooglePayConfigData getGooglePaySettings() {
        final WorldpayMerchantConfigData config = worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData();
        return config.getGooglePaySettings();
    }

    protected void invokeSuperFillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component) {
        super.fillModel(request, model, component);
    }
}
