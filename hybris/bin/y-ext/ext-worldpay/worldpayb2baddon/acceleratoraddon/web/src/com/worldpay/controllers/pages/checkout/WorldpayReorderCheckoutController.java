package com.worldpay.controllers.pages.checkout;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.b2bacceleratoraddon.controllers.pages.checkout.ReorderCheckoutController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.order.InvalidCartException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;

/**
 * Worldpay reorder checkout controller
 */
@Controller
@RequestMapping(value = "/checkout/worldpay/summary")
public class WorldpayReorderCheckoutController extends ReorderCheckoutController {

    /**
     * Handle reorder
     * @param orderCode
     * @param redirectModel
     * @return
     * @throws CMSItemNotFoundException
     * @throws InvalidCartException
     * @throws ParseException
     * @throws CommerceCartModificationException
     */
    @RequestMapping(value = "/reorder", method =
            {RequestMethod.PUT, RequestMethod.POST})
    @RequireHardLogIn
    public String reorder(@RequestParam(value = "orderCode") final String orderCode, final RedirectAttributes redirectModel)
            throws CMSItemNotFoundException, InvalidCartException, ParseException, CommerceCartModificationException {
        super.reorder(orderCode, redirectModel);
        return REDIRECT_PREFIX + "/checkout/multi/worldpay/summary/view";
    }
}
