/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.worldpay.controllers.cms;

import de.hybris.platform.addonsupport.controllers.cms.GenericCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Generic Base Class Controller for Addon CMS Components that populates the view model from attributes stores on the
 * CMS Component.
 */
public class WorldpayGenericCMSAddOnComponentController extends GenericCMSAddOnComponentController {
    @Override
    @RequestMapping
    public String handleGet(final HttpServletRequest request, final HttpServletResponse response, final Model model)
            throws Exception {
        return invokeSuperHandleGet(request, response, model);
    }

    protected String invokeSuperHandleGet(final HttpServletRequest request, final HttpServletResponse response, final Model model) throws Exception {
        return super.handleGet(request, response, model);
    }
}
