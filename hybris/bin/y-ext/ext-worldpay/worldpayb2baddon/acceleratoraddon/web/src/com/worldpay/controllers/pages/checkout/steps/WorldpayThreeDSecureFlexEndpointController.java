package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.constants.WorldpayapiConstants;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.forms.B2BCSEPaymentForm;
import com.worldpay.forms.ThreeDSecureFlexForm;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.addErrorMessage;

/**
 * Web controller to handle challenge response from Worldpay in 3DSecure flex flow
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay/3dsecureflex/sop")
public class WorldpayThreeDSecureFlexEndpointController extends AbstractWorldpayDirectCheckoutStepController {

    protected static final String REDIRECT_URL_QUOTE_ORDER_CONFIRMATION = REDIRECT_PREFIX + "/checkout/quote/orderConfirmation/";
    protected static final String REDIRECT_URL_REPLENISHMENT_CONFIRMATION = REDIRECT_PREFIX + "/checkout/replenishment/confirmation/";
    private static final Logger LOG = Logger.getLogger(WorldpayThreeDSecureFlexEndpointController.class);
    private static final String WORLDPAY_ADDON_PREFIX = "worldpay.addon.prefix";
    private static final String CHECKOUT_3DSECUREFLEX_RESPONSE_AUTOSUBMIT = "pages/checkout/multi/threedsflex/autosubmitThreeDSecureFlexResponse";
    private static final String UNDEFINED_PREFIX = "undefined";
    private static final String B2B_CSE_PAYMENT_FORM = "b2bCSEPaymentForm";

    @Resource
    private WorldpayAddonEndpointService worldpayAddonEndpointService;
    @Resource
    private ConfigurationService configurationService;
    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;

    @PostMapping(value = "/response")
    @RequireHardLogIn
    public String doHandleThreeDSecureResponse(final HttpServletRequest request, final ThreeDSecureFlexForm threeDSecureFlexForm, final RedirectAttributes redirectAttributes) {
        return REDIRECT_PREFIX + configurationService.getConfiguration().getString(WorldpayapiConstants.WORLDPAY_3DSECURE_FLEX_SECOND_AUTH_SUBMIT_URL);
    }

    @GetMapping(value = "/response/autosubmit")
    @RequireHardLogIn
    public String getThreeDSecureResponseAutosubmit(final HttpServletRequest request, final ThreeDSecureFlexForm threeDSecureFlexForm, final RedirectAttributes redirectAttributes) {
        return configurationService.getConfiguration().getString(WORLDPAY_ADDON_PREFIX, UNDEFINED_PREFIX) + CHECKOUT_3DSECUREFLEX_RESPONSE_AUTOSUBMIT;
    }

    /**
     * It handles the the second request to authorize the order in flex 3d secure flow. It triggers also the place order
     *
     * @param request - the HttpServletRequest
     * @param model   - the page model
     * @return String
     */
    @PostMapping(value = "/response/autosubmit")
    @RequireHardLogIn
    public String doHandleThreeDSecureResponse(final HttpServletRequest request, @ModelAttribute(B2B_CSE_PAYMENT_FORM) final B2BCSEPaymentForm b2bCSEPaymentForm, final Model model, final HttpServletResponse response) throws CMSItemNotFoundException {
        try {
            final DirectResponseData responseData = worldpayDirectOrderFacade.executeSecondPaymentAuthorisation3DSecure();
            if (AUTHORISED != responseData.getTransactionStatus()) {
                return handleDirectResponse(model, responseData, response);
            }
        } catch (final InvalidCartException | WorldpayException e) {
            GlobalMessages.addErrorMessage(model, CHECKOUT_ERROR_PAYMENTETHOD_FORMENTRY_INVALID);
            LOG.error("There was an error authorising the transaction", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return getErrorView(model);
        }

        // Terms have been validated in the summary checkout step controller
        final PlaceOrderData placeOrderData = new PlaceOrderData();
        placeOrderData.setTermsCheck(true);

        final AbstractOrderData orderData;
        try {
            orderData = getB2BCheckoutFacade().placeOrder(placeOrderData);
        } catch (final EntityValidationException e) {
            LOG.error("Failed to place Order", e);
            addErrorMessage(model, e.getLocalizedMessage());
            b2bCSEPaymentForm.setTermsCheck(false);
            model.addAttribute(b2bCSEPaymentForm);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return getErrorView(model);
        } catch (final Exception e) {
            LOG.error("Failed to place Order", e);
            addErrorMessage(model, "checkout.placeOrder.failed");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return getErrorView(model);
        }
        return redirectToOrderConfirmationPage(placeOrderData, orderData);
    }

    protected CheckoutFacade getB2BCheckoutFacade() {
        return (CheckoutFacade) this.getCheckoutFacade();
    }

    @Override
    protected String getErrorView(final Model model) throws CMSItemNotFoundException {
        setupAddPaymentPage(model);
        return worldpayAddonEndpointService.getCSEPaymentDetailsPage();
    }

    protected String redirectToOrderConfirmationPage(final PlaceOrderData placeOrderData, final AbstractOrderData orderData) {
        if (Boolean.TRUE.equals(placeOrderData.getNegotiateQuote())) {
            return REDIRECT_URL_QUOTE_ORDER_CONFIRMATION + orderData.getCode();
        } else if (Boolean.TRUE.equals(placeOrderData.getReplenishmentOrder()) && (orderData instanceof ScheduledCartData)) {
            return REDIRECT_URL_REPLENISHMENT_CONFIRMATION + ((ScheduledCartData) orderData).getJobCode();
        }
        return REDIRECT_URL_ORDER_CONFIRMATION + orderData.getCode();
    }
}
