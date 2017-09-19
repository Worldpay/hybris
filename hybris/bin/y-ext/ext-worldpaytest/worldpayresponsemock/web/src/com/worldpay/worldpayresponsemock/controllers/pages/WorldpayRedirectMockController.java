package com.worldpay.worldpayresponsemock.controllers.pages;

import com.worldpay.worldpayresponsemock.controllers.WorldpayResponseMockControllerConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller to handle lightbox redirect. With this you can emulate lightbox flow.
 */
@Controller
public class WorldpayRedirectMockController {

    public static final String STOREFRONT_PARAM = "storeFront";

    /**
     *
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping (value = "/redirect")
    public String handleLightboxRequest(Model model, HttpServletRequest request) {
        model.addAttribute(STOREFRONT_PARAM, request.getParameter("successURL"));

        return WorldpayResponseMockControllerConstants.Pages.Views.LIGHTBOX;
    }
}
