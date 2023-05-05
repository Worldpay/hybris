package com.worldpay.controllers.cms;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller("WorldpayCCComponentController")
@RequestMapping(value = "/view/WorldpayCCComponentController")
public class WorldpayCCComponentController extends WorldpayGenericCMSAddOnComponentController {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component) {
        invokeSuperFillModel(request, model, component);
    }

    protected void invokeSuperFillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component) {
        super.fillModel(request, model, component);
    }
}
