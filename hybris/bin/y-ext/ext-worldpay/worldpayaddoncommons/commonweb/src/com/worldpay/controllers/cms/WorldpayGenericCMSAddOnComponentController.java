package com.worldpay.controllers.cms;

import de.hybris.platform.addonsupport.controllers.cms.GenericCMSAddOnComponentController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Generic Base Class Controller for Addon CMS Components that populates the view model from attributes stores on the
 * CMS Component.
 */
public class WorldpayGenericCMSAddOnComponentController extends GenericCMSAddOnComponentController {
    @Override
    @RequestMapping
    public String handleGet(final HttpServletRequest request, final HttpServletResponse response, final Model model) throws Exception {
        return invokeSuperHandleGet(request, response, model);
    }

    protected String invokeSuperHandleGet(final HttpServletRequest request, final HttpServletResponse response, final Model model) throws Exception {
        return super.handleGet(request, response, model);
    }
}
