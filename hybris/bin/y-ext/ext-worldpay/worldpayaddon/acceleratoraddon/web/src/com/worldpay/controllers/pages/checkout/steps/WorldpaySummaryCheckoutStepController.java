package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PlaceOrderForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.order.InvalidCartException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants.BREADCRUMBS_KEY;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.*;
import static de.hybris.platform.commercefacades.product.ProductOption.BASIC;
import static de.hybris.platform.commercefacades.product.ProductOption.PRICE;
import static java.text.MessageFormat.format;

/**
 * Web controller to handle a summary in a checkout step
 */
@Controller
@RequestMapping (value = "/checkout/multi/worldpay/summary")
public class WorldpaySummaryCheckoutStepController extends AbstractWorldpayDirectCheckoutStepController {

    private static final Logger LOGGER = Logger.getLogger(WorldpayPaymentMethodCheckoutStepController.class);

    protected static final String SUMMARY = "summary";
    protected static final String CART_SUFFIX = "/cart";
    protected static final String CART_DATA = "cartData";
    protected static final String ALL_ITEMS = "allItems";
    protected static final String DELIVERY_ADDRESS = "deliveryAddress";
    protected static final String DELIVERY_MODE = "deliveryMode";
    protected static final String PAYMENT_INFO = "paymentInfo";
    protected static final String REQUEST_SECURITY_CODE = "requestSecurityCode";
    protected static final String META_ROBOTS = "metaRobots";
    protected static final String CHECKOUT_MULTI_SUMMARY_BREADCRUMB = "checkout.multi.summary.breadcrumb";
    protected static final String REDIRECT_URL_CHOOSE_PAYMENT_METHOD = REDIRECT_PREFIX + "/checkout/multi/worldpay/choose-payment-method";

    @Resource (name = "worldpayCheckoutFacade")
    private AcceleratorCheckoutFacade checkoutFacade;
    @Resource (name = "worldpayCheckoutFacade")
    private CheckoutFlowFacade checkoutFlowFacade;
    @Resource
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;
    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    private WorldpayAddonEndpointService worldpayAddonEndpointService;

    /**
     * {@inheritDoc}
     */
    @RequestMapping (value = "/view", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    @PreValidateCheckoutStep (checkoutStep = SUMMARY)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
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

        model.addAttribute(new PlaceOrderForm());

        final ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
        storeCmsPageInModel(model, contentPageForLabelOrId);
        setUpMetaDataForContentPage(model, contentPageForLabelOrId);
        model.addAttribute(BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs(CHECKOUT_MULTI_SUMMARY_BREADCRUMB));
        model.addAttribute(META_ROBOTS, "noindex,nofollow");
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        final String subscriptionId = cartData.getPaymentInfo().getSubscriptionId();
        model.addAttribute(REQUEST_SECURITY_CODE, StringUtils.isNotBlank(subscriptionId));
        return worldpayAddonEndpointService.getCheckoutSummaryPage();
    }

    /**
     * Validates cart, creates an order and redirects to order confirmation page
     *
     * @param placeOrderForm
     * @param model
     * @param request
     * @param redirectAttributes
     * @return
     * @throws CMSItemNotFoundException
     */
    @RequestMapping (value = "/placeOrder")
    @RequireHardLogIn
    public String placeOrder(@ModelAttribute ("placeOrderForm") final PlaceOrderForm placeOrderForm, final Model model,
                             final HttpServletRequest request, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {

        if (validateCart(redirectAttributes)) {
            // Invalid cart. Bounce back to the cart page.
            return REDIRECT_PREFIX + CART_SUFFIX;
        }

        if (!isOrderFormValid(placeOrderForm, model)) {
            // redirect to summary
            return enterStep(model, redirectAttributes);
        }

        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = getWorldpayAdditionalInfo(request, placeOrderForm.getSecurityCode());

        return handleRespone(model, worldpayAdditionalInfoData);
    }

    private String handleRespone(Model model, WorldpayAdditionalInfoData worldpayAdditionalInfoData) throws CMSItemNotFoundException {
        try {
            final DirectResponseData directResponseData = worldpayDirectOrderFacade.authoriseRecurringPayment(worldpayAdditionalInfoData);
            return handleDirectResponse(model, directResponseData);
        } catch (InvalidCartException | WorldpayException e) {
            addErrorMessage(model, CHECKOUT_ERROR_PAYMENTETHOD_FORMENTRY_INVALID);
            LOGGER.error("There was an error authorising the transaction", e);
            return getErrorView(model);
        }
    }

    protected String getErrorView(final Model model) throws CMSItemNotFoundException {
        setupAddPaymentPage(model);
        return worldpayAddonEndpointService.getCheckoutSummaryPage();
    }

    /**
     * Validates the order form to filter out invalid order states
     *
     * @param placeOrderForm The spring form of the order being submitted
     * @param model          A spring Model
     * @return True if the order form is invalid and false if everything is valid.
     */
    protected boolean isOrderFormValid(PlaceOrderForm placeOrderForm, Model model) {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        final String subscriptionId = cartData.getPaymentInfo().getSubscriptionId();
        final String securityCode = placeOrderForm.getSecurityCode();

        return !hasSubscriptionId(subscriptionId, securityCode, model) &&
                !haveNoDeliveryAddress(model) &&
                !haveNoDeliveryMethod(model) &&
                !haveNoPaymentMethod(model) &&
                !haveNoTermsAccepted(placeOrderForm, model) &&
                !hasTaxCalculation(cartData, model) &&
                !hasCalculateFlag(cartData, model);
    }

    private boolean hasSubscriptionId(String subscriptionId, String securityCode, Model model){
        if (subscriptionId != null && StringUtils.isBlank(securityCode)) {
            addErrorMessage(model, "checkout.paymentMethod.noSecurityCode");
            return true;
        }
        return false;
    }

    private boolean haveNoDeliveryAddress(Model model){
        if (getCheckoutFlowFacade().hasNoDeliveryAddress()) {
            addErrorMessage(model, "checkout.deliveryAddress.notSelected");
            return true;
        }
        return false;
    }

    private boolean haveNoDeliveryMethod(Model model){
        if (getCheckoutFlowFacade().hasNoDeliveryMode()) {
            addErrorMessage(model, "checkout.deliveryMethod.notSelected");
            return true;
        }
        return false;
    }

    private boolean haveNoPaymentMethod(Model model){
        if (getCheckoutFlowFacade().hasNoPaymentInfo()) {
            addErrorMessage(model, "checkout.paymentMethod.notSelected");
            return true;
        }
        return false;
    }

    private boolean haveNoTermsAccepted(PlaceOrderForm placeOrderForm, Model model){
        if (!placeOrderForm.isTermsCheck()) {
            addErrorMessage(model, "checkout.error.terms.not.accepted");
            return true;
        }
        return false;
    }

    private boolean hasTaxCalculation(CartData cartData, Model model){
        if (!getCheckoutFacade().containsTaxValues()) {
            LOGGER.error(format(
                    "Cart {0} does not have any tax values, which means the tax calculation was not properly done, placement of order can't continue",
                    cartData.getCode()));
            addErrorMessage(model, "checkout.error.tax.missing");
            return true;
        }
        return false;
    }

    private boolean hasCalculateFlag(CartData cartData, Model model){
        if (!cartData.isCalculated()) {
            LOGGER.error(format("Cart {0} has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
            addErrorMessage(model, "checkout.error.cart.notcalculated");
            return true;
        }
        return false;
    }

    /**
     *
     * @param model             the {@Model} to be used
     * @param redirectModel     the {@RedirectAttributes} to be used
     * @return
     * @throws CMSItemNotFoundException
     */
    @RequestMapping (value = "/express", method = RequestMethod.GET)
    @RequireHardLogIn
    public String performExpressCheckout(final Model model, final RedirectAttributes redirectModel)
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
                    returnCode = enterStep(model, redirectModel);
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

    @RequestMapping (value = "/back", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    public String back(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().previousStep();
    }

    @RequestMapping (value = "/next", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    public String next(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().nextStep();
    }

    protected CheckoutStep getCheckoutStep() {
        return getCheckoutStep(SUMMARY);
    }

    private WorldpayAdditionalInfoData getWorldpayAdditionalInfo(final HttpServletRequest request,
                                                                 final String securityCode) {
        final WorldpayAdditionalInfoData info = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
        info.setSecurityCode(securityCode);
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
