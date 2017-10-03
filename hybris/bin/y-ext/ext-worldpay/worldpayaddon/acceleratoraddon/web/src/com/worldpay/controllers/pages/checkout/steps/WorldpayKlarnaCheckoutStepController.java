package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayKlarnaPaymentCheckoutFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.hostedorderpage.data.KlarnaRedirectAuthoriseResult;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.order.InvalidCartException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;

import static com.worldpay.service.model.AuthorisedStatus.ERROR;

/**
 * Web controller to handle Klarna APM
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay")
public class WorldpayKlarnaCheckoutStepController extends AbstractController {
    private static final Logger LOG = Logger.getLogger(WorldpayHopResponseController.class);
    private static final String KLARNA_RESPONSE_PAGE_DATA_PARAM = "KLARNA_VIEW_DATA";
    private static final String CHECKOUT_PLACE_ORDER_FAILED = "checkout.placeOrder.failed";
    private static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";
    protected static final String REDIRECT_URL_CHOOSE_PAYMENT_METHOD = REDIRECT_PREFIX + "/checkout/multi/worldpay/choose-payment-method";

    @Resource
    private WorldpayKlarnaPaymentCheckoutFacade worldpayKlarnaPaymentCheckoutFacade;
    @Resource
    private WorldpayHostedOrderFacade worldpayHostedOrderFacade;
    @Resource
    private WorldpayAddonEndpointService worldpayAddonEndpointService;
    @Resource (name = "worldpayCheckoutFacade")
    private AcceleratorCheckoutFacade checkoutFacade;

    /**
     * Handles the place order submit from Klarna page
     *
     * @param model              the {@link Model} to be used
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/klarna/confirmation", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doHandleKlarnaConfirmation(final Model model, final RedirectAttributes redirectAttributes) {
        try {
            final KlarnaRedirectAuthoriseResult klarnaRedirectAuthoriseResult = worldpayKlarnaPaymentCheckoutFacade.checkKlarnaOrderStatus();
            worldpayHostedOrderFacade.completeRedirectAuthorise(klarnaRedirectAuthoriseResult);
            checkoutFacade.placeOrder();
            final String decodedHTMLContent = klarnaRedirectAuthoriseResult.getDecodedHTMLContent();
            model.addAttribute(KLARNA_RESPONSE_PAGE_DATA_PARAM, decodedHTMLContent);
            return worldpayAddonEndpointService.getKlarnaResponsePage();
        } catch (WorldpayException | InvalidCartException e) {
            LOG.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, CHECKOUT_PLACE_ORDER_FAILED);
            return doHostedOrderPageError(ERROR.getCode(), redirectAttributes);
        }
    }

    private String doHostedOrderPageError(final String paymentStatus, final RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, paymentStatus != null ? paymentStatus : ERROR.getCode());
        return REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=" + paymentStatus;
    }


}
