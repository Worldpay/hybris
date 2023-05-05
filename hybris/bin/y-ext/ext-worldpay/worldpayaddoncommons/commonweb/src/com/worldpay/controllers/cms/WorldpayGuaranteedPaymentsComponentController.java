package com.worldpay.controllers.cms;

import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import de.hybris.platform.addonsupport.controllers.cms.GenericCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller("WorldpayGuaranteedPaymentsComponentController")
@RequestMapping(value = "/view/WorldpayGuaranteedPaymentsComponentController")
public class WorldpayGuaranteedPaymentsComponentController extends WorldpayGenericCMSAddOnComponentController {

    private static final String IS_ENABLED = "isEnabled";
    private static final String SCRIPT = "script";
    private static final String SCRIPT_URL = "worldpay.guaranteed.payments.script.url";
    private static final String SESSION_ID = "sessionId";

    @Resource
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;
    @Resource
    private ConfigurationService configurationService;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component) {
        invokeSuperFillModel(request, model, component);
        model.addAttribute(IS_ENABLED, worldpayPaymentCheckoutFacade.isGPEnabled());
        model.addAttribute(SCRIPT, configurationService.getConfiguration().getString(SCRIPT_URL));
        model.addAttribute(SESSION_ID, worldpayPaymentCheckoutFacade.createCheckoutId());
    }

    protected void invokeSuperFillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component) {
        super.fillModel(request, model, component);
    }

}
