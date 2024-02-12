package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.data.WorldpayAPMPaymentInfoData;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.forms.CSEPaymentForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.order.InvalidCartException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

import static de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants.BREADCRUMBS_KEY;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.*;
import static de.hybris.platform.commercefacades.product.ProductOption.BASIC;
import static de.hybris.platform.commercefacades.product.ProductOption.PRICE;

/**
 * Web controller to handle a summary in a checkout step
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay/summary")
@SuppressWarnings({"java:S110","Duplicates","java:S1854"})
public class WorldpaySummaryCheckoutStepController extends AbstractWorldpayDirectCheckoutStepController {

    protected static final Logger LOG = LogManager.getLogger(WorldpaySummaryCheckoutStepController.class);
    protected static final String SUMMARY = "summary";
    protected static final String CART_SUFFIX = "/cart";
    protected static final String CART_DATA = "cartData";
    protected static final String ALL_ITEMS = "allItems";
    protected static final String BIN = "bin";
    protected static final String DELIVERY_ADDRESS = "deliveryAddress";
    protected static final String DELIVERY_MODE = "deliveryMode";
    protected static final String PAYMENT_INFO = "paymentInfo";
    protected static final String REQUEST_SECURITY_CODE = "requestSecurityCode";
    protected static final String SUBSCRIPTION_ID = "subscriptionId";
    protected static final String META_ROBOTS = "metaRobots";
    protected static final String CHECKOUT_MULTI_SUMMARY_BREADCRUMB = "checkout.multi.summary.breadcrumb";
    protected static final String REDIRECT_URL_CHOOSE_PAYMENT_METHOD = REDIRECT_PREFIX + "/checkout/multi/worldpay/choose-payment-method";
    protected static final String CSE_PAYMENT_FORM = "csePaymentForm";

    @Resource(name = "worldpayCheckoutFacade")
    private CheckoutFlowFacade checkoutFlowFacade;
    @Resource
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;
    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;

    /**
     * {@inheritDoc}
     */
    @GetMapping(value = "/view")
    @RequireHardLogIn
    @PreValidateCheckoutStep(checkoutStep = SUMMARY)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes, final HttpServletResponse response) throws CMSItemNotFoundException {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        if (CollectionUtils.isNotEmpty(cartData.getEntries())) {
            for (final OrderEntryData entry : cartData.getEntries()) {
                final String productCode = entry.getProduct().getCode();
                final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode, Arrays.asList(BASIC, PRICE));
                entry.setProduct(product);
            }
        }

        model.addAttribute(CART_DATA, cartData);
        model.addAttribute(ALL_ITEMS, cartData.getEntries());
        model.addAttribute(DELIVERY_ADDRESS, cartData.getDeliveryAddress());
        model.addAttribute(DELIVERY_MODE, cartData.getDeliveryMode());
        model.addAttribute(PAYMENT_INFO, cartData.getPaymentInfo());
        model.addAttribute(CSE_PAYMENT_FORM, new CSEPaymentForm());

        final ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
        storeCmsPageInModel(model, contentPageForLabelOrId);
        setUpMetaDataForContentPage(model, contentPageForLabelOrId);
        model.addAttribute(BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs(CHECKOUT_MULTI_SUMMARY_BREADCRUMB));
        model.addAttribute(META_ROBOTS, "noindex,nofollow");
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        final String subscriptionId = getSubscriptionId(cartData);
        model.addAttribute(REQUEST_SECURITY_CODE, StringUtils.isNotBlank(subscriptionId) && cartData.getPaymentInfo() != null && cartData.getWorldpayAPMPaymentInfo() == null);
        model.addAttribute(SUBSCRIPTION_ID, subscriptionId);
        Optional.ofNullable(cartData.getPaymentInfo())
                .map(CCPaymentInfoData::getBin)
                .ifPresent(bin -> model.addAttribute(BIN, bin));
        return worldpayAddonEndpointService.getCheckoutSummaryPage();
    }

    /**
     * Validates cart, creates an order and redirects to order confirmation page
     *
     * @param csePaymentForm
     * @param model
     * @param request
     * @param redirectAttributes
     * @return
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/placeOrder")
    @RequireHardLogIn
    public String placeOrder(@ModelAttribute("csePaymentForm") final CSEPaymentForm csePaymentForm, final Model model,
                             final HttpServletRequest request, final RedirectAttributes redirectAttributes, final HttpServletResponse response) throws CMSItemNotFoundException {

        if (validateCart(redirectAttributes)) {
            // Invalid cart. Bounce back to the cart page.
            return REDIRECT_PREFIX + CART_SUFFIX;
        }

        if (!isOrderFormValid(csePaymentForm, model)) {
            // redirect to summary
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return enterStep(model, redirectAttributes, response);
        }

        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = createCSESubscriptionAdditionalAuthInfo(csePaymentForm);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = getWorldpayAdditionalInfo(request, csePaymentForm.getSecurityCode(), cseAdditionalAuthInfo);

        return authorisePayment(model, worldpayAdditionalInfoData, response);
    }

    /**
     * @param model         the {@Model} to be used
     * @param redirectModel the {@RedirectAttributes} to be used
     * @return
     * @throws CMSItemNotFoundException
     */
    @GetMapping(value = "/express")
    @RequireHardLogIn
    public String performExpressCheckout(final Model model, final RedirectAttributes redirectModel, final HttpServletResponse response)
            throws CMSItemNotFoundException {
        if (getSessionService().getAttribute(WebConstants.CART_RESTORATION) != null
                && CollectionUtils.isNotEmpty(((CartRestorationData) getSessionService().getAttribute(WebConstants.CART_RESTORATION))
                .getModifications())) {
            return REDIRECT_URL_CART;
        }

        String returnCode = REDIRECT_URL_CART;

        if (getCheckoutFlowFacade().hasValidCart()) {
            switch (getCheckoutFacade().performExpressCheckout()) {
                case SUCCESS:
                    returnCode = enterStep(model, redirectModel, response);
                    break;
                case ERROR_DELIVERY_ADDRESS:
                    addFlashMessage(redirectModel, ERROR_MESSAGES_HOLDER, "checkout.express.error.deliveryAddress");
                    returnCode = REDIRECT_URL_ADD_DELIVERY_ADDRESS;
                    break;
                case ERROR_DELIVERY_MODE:
                case ERROR_CHEAPEST_DELIVERY_MODE:
                    addFlashMessage(redirectModel, ERROR_MESSAGES_HOLDER, "checkout.express.error.deliveryMode");
                    returnCode = REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
                    break;
                case ERROR_PAYMENT_INFO:
                    addFlashMessage(redirectModel, ERROR_MESSAGES_HOLDER, "checkout.express.error.paymentInfo");
                    returnCode = REDIRECT_URL_CHOOSE_PAYMENT_METHOD;
                    break;
                default:
                    addFlashMessage(redirectModel, ERROR_MESSAGES_HOLDER, "checkout.express.error.notAvailable");
            }
        }
        return returnCode;
    }

    /**
     * Returns the DDC collection iframe for 3d secure 2 payments
     *
     * @param model
     * @return the iframe
     */
    @GetMapping(value = "/DDCIframe")
    @RequireHardLogIn
    public String getDDCIframeContent(final Model model) {
        setDDCIframeData(model);
        return worldpayAddonEndpointService.getDdcIframe3dSecureFlex();
    }

    /**
     * Returns the DDC collection iframe for 3d secure 2 payments
     *
     * @param model
     * @return the iframe
     */
    @GetMapping(value = "/challengeIframe")
    @RequireHardLogIn
    public String getChallengeIframeContent(final Model model) {
        return worldpayAddonEndpointService.getChallengeIframe3dSecureFlex();
    }

    protected String authorisePayment(final Model model, final WorldpayAdditionalInfoData worldpayAdditionalInfoData, final HttpServletResponse response) throws CMSItemNotFoundException {
        try {
            final DirectResponseData directResponseData = worldpayDirectOrderFacade.authoriseRecurringPayment(worldpayAdditionalInfoData);
            return handleDirectResponse(model, directResponseData, response);
        } catch (final InvalidCartException | WorldpayException e) {
            addErrorMessage(model, CHECKOUT_ERROR_PAYMENTETHOD_FORMENTRY_INVALID);
            LOG.error("There was an error authorising the transaction", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return getErrorView(model);
        }
    }

    @Override
    protected String getErrorView(final Model model) throws CMSItemNotFoundException {
        setupAddPaymentPage(model);
        return worldpayAddonEndpointService.getCheckoutSummaryPage();
    }

    /**
     * Validates the order form to filter out invalid order states
     *
     * @param form  The spring form of the order being submitted
     * @param model A spring Model
     * @return True if the order form is invalid and false if everything is valid.
     */
    protected boolean isOrderFormValid(final CSEPaymentForm form, final Model model) {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        return arePaymentInfoValid(model, cartData, form) &&
                areDeliveryInfoValid(model) &&
                hasTermsAccepted(form, model) &&
                isCartValid(model, cartData);
    }

    /**
     * Test if the payment info for the given cart is valid
     *
     * @param model    A spring Model
     * @param cartData the cart to check
     * @param form     The spring form of the order being submitted
     * @return true if payment info is valid
     */
    protected boolean arePaymentInfoValid(final Model model, final CartData cartData, final CSEPaymentForm form) {
        return isSecurityCodeNeeded(model, cartData, form) && hasPaymentInfo(model);
    }

    protected String getSubscriptionId(final CartData cartData) {
        return Optional.ofNullable(cartData.getPaymentInfo())
                .map(CCPaymentInfoData::getSubscriptionId)
                .orElseGet(() -> Optional.ofNullable(cartData.getWorldpayAPMPaymentInfo())
                        .map(WorldpayAPMPaymentInfoData::getSubscriptionId)
                        .orElse(null)
                );
    }

    protected boolean areDeliveryInfoValid(final Model model) {
        return hasDeliveryAddress(model) &&
                hasDeliveryMode(model);
    }


    protected boolean isCartValid(final Model model, final CartData cartData) {
        return hasTaxCalculated(cartData, model) &&
                isCartCalculated(cartData, model);
    }

    /**
     * Only needed for payment with card, not with tokenised APM as Paypal.
     *
     * @param model
     * @param cartData the cart to check
     * @param form
     * @return true if securityCode is needed
     */
    protected boolean isSecurityCodeNeeded(final Model model, final CartData cartData, final CSEPaymentForm form) {
        final String securityCode = form.getSecurityCode();
        if (StringUtils.isBlank(securityCode) && cartData.getPaymentInfo() != null && cartData.getWorldpayAPMPaymentInfo() == null) {
            addErrorMessage(model, "checkout.paymentMethod.noSecurityCode");
            return false;
        }
        return true;
    }


    private boolean hasTermsAccepted(final CSEPaymentForm form, final Model model) {
        if (!form.isTermsCheck()) {
            addErrorMessage(model, "checkout.error.terms.not.accepted");
            return false;
        }
        return true;
    }

    private boolean hasTaxCalculated(final CartData cartData, final Model model) {
        if (!getCheckoutFacade().containsTaxValues()) {
            LOG.error(
                    "Cart {} does not have any tax values, which means the tax calculation was not properly done, placement of order can\'t continue",
                    cartData::getCode);
            addErrorMessage(model, "checkout.error.tax.missing");
            return false;
        }
        return true;
    }

    private boolean isCartCalculated(final CartData cartData, final Model model) {
        if (!cartData.isCalculated()) {
            LOG.error("Cart {} has a calculated flag of FALSE, placement of order can\'t continue", cartData::getCode);
            addErrorMessage(model, "checkout.error.cart.notcalculated");
            return false;
        }
        return true;
    }

    @Override
    protected CheckoutStep getCheckoutStep() {
        return getCheckoutStep(SUMMARY);
    }

    private WorldpayAdditionalInfoData getWorldpayAdditionalInfo(final HttpServletRequest request,
                                                                 final String securityCode, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) {
        final WorldpayAdditionalInfoData info = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
        info.setSecurityCode(securityCode);
        Optional
                .ofNullable(cseAdditionalAuthInfo.getAdditional3DS2())
                .ifPresent(info::setAdditional3DS2);
        return info;
    }

    @Override
    protected AcceleratorCheckoutFacade getCheckoutFacade() {
        return this.checkoutFacade;
    }

    @Override
    public CheckoutFlowFacade getCheckoutFlowFacade() {
        return checkoutFlowFacade;
    }
}
