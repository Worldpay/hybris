package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.constants.WorldpayapiConstants;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.forms.CSEPaymentForm;
import com.worldpay.forms.ThreeDSecureFlexForm;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Web controller to handle challenge response from Worldpay in 3DSecure flex flow
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay/3dsecureflex/sop")
public class WorldpayThreeDSecureFlexEndpointController extends AbstractWorldpayDirectCheckoutStepController {

    private static final Logger LOG = LogManager.getLogger(WorldpayThreeDSecureFlexEndpointController.class);

    private static final String WORLDPAY_ADDON_PREFIX = "worldpay.addon.prefix";
    private static final String CHECKOUT_3DSECUREFLEX_RESPONSE_AUTOSUBMIT = "pages/checkout/multi/threedsflex/autosubmitThreeDSecureFlexResponse";
    private static final String UNDEFINED_PREFIX = "undefined";
    private static final String CSE_PAYMENT_FORM = "csePaymentForm";

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
     * @param model - the page model
     * @return String
     */
    @PostMapping(value = "/response/autosubmit")
    @RequireHardLogIn
    public String doHandleThreeDSecureResponse(final HttpServletRequest request, final Model model, final HttpServletResponse response) throws CMSItemNotFoundException {
        try {
            final DirectResponseData responseData = worldpayDirectOrderFacade.executeSecondPaymentAuthorisation3DSecure();
            return handleDirectResponse(model, responseData, response);
        } catch (final InvalidCartException | WorldpayException e) {
            GlobalMessages.addErrorMessage(model, CHECKOUT_ERROR_PAYMENTETHOD_FORMENTRY_INVALID);
            LOG.error("There was an error authorising the transaction", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return getErrorView(model);
        }
    }

    @Override
    protected String getErrorView(final Model model) throws CMSItemNotFoundException {
        setupAddPaymentPage(model);
        model.addAttribute(CSE_PAYMENT_FORM, new CSEPaymentForm());
        return worldpayAddonEndpointService.getCSEPaymentDetailsPage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException {
        super.setupAddPaymentPage(model);
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        model.addAttribute("cartData", cartData);
        model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
        model.addAttribute(PAYMENT_INFOS, getUserFacade().getCCPaymentInfos(true));
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs(CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB));
    }
}
