package com.worldpay.worldpayresponsemock.controllers.pages;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/3dresponse")
public class Worldpay3DResponseMockController {

    @RequestMapping (method = POST)
    public String mockWorldpayResponse(final ModelMap model, final HttpServletRequest request) {

        String paRes = request.getParameter("PaRes");
        String merchantData = request.getParameter("MD");

        model.put("paRes", paRes);
        model.put("merchantData", merchantData);

        return "pages/threeDSecureResponse";
    }

}
