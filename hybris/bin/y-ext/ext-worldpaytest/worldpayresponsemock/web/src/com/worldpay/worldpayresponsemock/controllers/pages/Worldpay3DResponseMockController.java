package com.worldpay.worldpayresponsemock.controllers.pages;

import static com.worldpay.worldpayresponsemock.controllers.WorldpayResponseMockControllerConstants.Pages.Views.THREED_SECURE_RESPONSE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to mock 3D response from Worldpay. With this you can emulate 3D secure response.
 */
@Controller
@RequestMapping(value = "/3dresponse")
public class Worldpay3DResponseMockController {

    /**
     *
     * @param model
     * @param request
     * @return
     */
    @PostMapping
    public String mockWorldpayResponse(final ModelMap model, final HttpServletRequest request) {

        String paRes = request.getParameter("PaRes");
        String merchantData = request.getParameter("MD");

        model.put("paRes", paRes);
        model.put("merchantData", merchantData);

        return THREED_SECURE_RESPONSE;
    }

}
