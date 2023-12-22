package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.forms.B2BCSEPaymentForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.order.InvalidCartException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.addErrorMessage;

/**
 * Worldpay summary checkout step controller
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay/summary")
@SuppressWarnings({"java:S110","Duplicates","java:S1854"})
public class WorldpayB2BSummaryCheckoutStepController extends AbstractWorldpayDirectCheckoutStepController {

    private static final Logger LOG = LogManager.getLogger(WorldpayB2BSummaryCheckoutStepController.class);

    private static final String SUMMARY = "summary";
    private static final String CART_SUFFIX = "/cart";
    private static final String REDIRECT_URL_QUOTE_ORDER_CONFIRMATION = REDIRECT_PREFIX + "/checkout/quote/orderConfirmation/";
    private static final String REDIRECT_URL_REPLENISHMENT_CONFIRMATION = REDIRECT_PREFIX + "/checkout/replenishment/confirmation/";
    private static final String TEXT_STORE_DATEFORMAT_KEY = "text.store.dateformat";
    private static final String DEFAULT_DATEFORMAT = "MM/dd/yyyy";
    private static final String BIN = "bin";
    private static final int NDAYS_END = 30;
    private static final int NTHDAYOFMONTH_END = 31;
    private static final int NTHWEEK_END = 12;
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final String CART_DATA = "cartData";
    private static final String ALL_ITEMS = "allItems";
    private static final String DELIVERY_ADDRESS = "deliveryAddress";
    private static final String DELIVERY_MODE = "deliveryMode";
    private static final String PAYMENT_INFO = "paymentInfo";
    private static final String REQUEST_SECURITY_CODE = "requestSecurityCode";
    private static final String B2B_CSE_PAYMENT_FORM = "b2bCSEPaymentForm";

    @Resource
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;
    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;

    /**
     * Enter step
     *
     * @param model
     * @param redirectAttributes
     * @return
     * @throws CMSItemNotFoundException
     */
    @GetMapping(value = "/view")
    @RequireHardLogIn
    @PreValidateCheckoutStep(checkoutStep = SUMMARY)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes, final HttpServletResponse response) throws CMSItemNotFoundException {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        if (cartData.getEntries() != null && !cartData.getEntries().isEmpty()) {
            for (final OrderEntryData entry : cartData.getEntries()) {
                final String productCode = entry.getProduct().getCode();
                final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode,
                        Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.VARIANT_MATRIX_BASE, ProductOption.PRICE_RANGE));
                entry.setProduct(product);
            }
        }

        model.addAttribute(CART_DATA, cartData);
        model.addAttribute(ALL_ITEMS, cartData.getEntries());
        model.addAttribute(DELIVERY_ADDRESS, cartData.getDeliveryAddress());
        model.addAttribute(DELIVERY_MODE, cartData.getDeliveryMode());
        model.addAttribute(PAYMENT_INFO, cartData.getPaymentInfo());
        addScheduleSetupToModel(model);
        model.addAttribute(REQUEST_SECURITY_CODE, getRequestSecurityCodeValue());

        final B2BCSEPaymentForm b2bCSEPaymentForm = new B2BCSEPaymentForm();
        b2bCSEPaymentForm.setReplenishmentRecurrence(B2BReplenishmentRecurrenceEnum.MONTHLY);
        b2bCSEPaymentForm.setnDays("14");
        final List<DayOfWeek> daysOfWeek = new ArrayList<>();
        daysOfWeek.add(DayOfWeek.MONDAY);
        b2bCSEPaymentForm.setnDaysOfWeek(daysOfWeek);
        if (worldpayPaymentCheckoutFacade.isFSEnabled()) {
            b2bCSEPaymentForm.setDateOfBirth((Date) model.asMap().get(BIRTHDAY_DATE));
        }
        if (worldpayPaymentCheckoutFacade.isFSEnabled() || worldpayPaymentCheckoutFacade.isGPEnabled()) {
            b2bCSEPaymentForm.setDeviceSession((String) model.asMap().get(DEVICE_SESSION));
        }

        model.addAttribute(B2B_CSE_PAYMENT_FORM, b2bCSEPaymentForm);

        storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        final String subscriptionId = cartData.getPaymentInfo() != null ? cartData.getPaymentInfo().getSubscriptionId() : null;
        model.addAttribute(SUBSCRIPTION_ID, subscriptionId);

        Optional.ofNullable(cartData.getPaymentInfo()).ifPresent(bin -> model.addAttribute(BIN, bin));

        return worldpayAddonEndpointService.getCheckoutSummaryPage();
    }

    protected void addScheduleSetupToModel(Model model) {
        model.addAttribute("nDays", getNumberRange(1, NDAYS_END));
        model.addAttribute("nthDayOfMonth", getNumberRange(1, NTHDAYOFMONTH_END));
        model.addAttribute("nthWeek", getNumberRange(1, NTHWEEK_END));
        model.addAttribute("daysOfWeek", getB2BCheckoutFacade().getDaysOfWeekForReplenishmentCheckoutSummary());
    }

    protected List<String> getNumberRange(final int startNumber, final int endNumber) {
        final List<String> numbers = new ArrayList<>();
        for (int number = startNumber; number <= endNumber; number++) {
            numbers.add(String.valueOf(number));
        }
        return numbers;
    }

    /**
     * Validates cart, creates an order and redirects to order confirmation page
     *
     * @param b2bCSEPaymentForm
     * @param model
     * @param request
     * @param redirectAttributes
     * @return
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/placeOrder")
    @RequireHardLogIn
    public String placeOrder(@ModelAttribute(B2B_CSE_PAYMENT_FORM) final B2BCSEPaymentForm b2bCSEPaymentForm, final Model model, final HttpServletRequest request,
                             final RedirectAttributes redirectAttributes, final HttpServletResponse response) throws CMSItemNotFoundException, InvalidCartException {

        if (validateCart(redirectAttributes)) {
            // Invalid cart. Bounce back to the cart page.
            return REDIRECT_PREFIX + CART_SUFFIX;
        }

        if (!isOrderFormValid(b2bCSEPaymentForm, model)) {
            // redirect to summary
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return enterStep(model, redirectAttributes, response);
        }

        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = createCSESubscriptionAdditionalAuthInfo(b2bCSEPaymentForm);
        if (getCheckoutFacade().getCheckoutCart().getPaymentInfo() != null && !b2bCSEPaymentForm.isReplenishmentOrder()) {
            // Pay by credit card - Place Order Now - authorize payment
            final WorldpayAdditionalInfoData worldpayAdditionalInfoData = getWorldpayAdditionalInfo(request, b2bCSEPaymentForm, cseAdditionalAuthInfo);
            try {
                final DirectResponseData directResponseData = worldpayDirectOrderFacade.authoriseRecurringPayment(worldpayAdditionalInfoData);
                if (AUTHORISED != directResponseData.getTransactionStatus()) {
                    return handleDirectResponse(model, directResponseData, response);
                }
            } catch (WorldpayException e) {
                LOG.error("There was an error authorising the transaction", e);
                addErrorMessage(model, "checkout.placeOrder.failed");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return getErrorView(model);
            }
        }

        final PlaceOrderData placeOrderData = new PlaceOrderData();
        placeOrderData.setNDays(b2bCSEPaymentForm.getnDays());
        placeOrderData.setNDaysOfWeek(b2bCSEPaymentForm.getnDaysOfWeek());
        placeOrderData.setNthDayOfMonth(b2bCSEPaymentForm.getNthDayOfMonth());
        placeOrderData.setNWeeks(b2bCSEPaymentForm.getnWeeks());
        placeOrderData.setReplenishmentOrder(b2bCSEPaymentForm.isReplenishmentOrder());
        placeOrderData.setReplenishmentRecurrence(b2bCSEPaymentForm.getReplenishmentRecurrence());
        placeOrderData.setReplenishmentStartDate(b2bCSEPaymentForm.getReplenishmentStartDate());
        placeOrderData.setSecurityCode(b2bCSEPaymentForm.getSecurityCode());
        placeOrderData.setTermsCheck(b2bCSEPaymentForm.isTermsCheck());

        final AbstractOrderData orderData;
        try {
            orderData = getB2BCheckoutFacade().placeOrder(placeOrderData);
        } catch (final EntityValidationException e) {
            LOG.error("Failed to place Order", e);
            addErrorMessage(model, e.getLocalizedMessage());

            b2bCSEPaymentForm.setTermsCheck(false);
            model.addAttribute(b2bCSEPaymentForm);

            return getErrorView(model);
        } catch (final Exception e) {
            LOG.error("Failed to place Order", e);
            addErrorMessage(model, "checkout.placeOrder.failed");
            return getErrorView(model);
        }
        return redirectToOrderConfirmationPage(placeOrderData, orderData);
    }

    protected boolean getRequestSecurityCodeValue() {
        // Only request the security code if using a saved card
        Boolean savedCardSelected = getSessionService().getAttribute(SAVED_CARD_SELECTED_ATTRIBUTE);
        return savedCardSelected != null && savedCardSelected;
    }

    /**
     * Validates the order form to filter out invalid order states
     *
     * @param b2bCSEPaymentForm The spring form of the order being submitted
     * @param model             A spring Model
     * @return True if the order form is invalid and false if everything is valid.
     */
    protected boolean isOrderFormValid(final B2BCSEPaymentForm b2bCSEPaymentForm, final Model model) {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        final String subscriptionId = cartData.getPaymentInfo() != null ? cartData.getPaymentInfo().getSubscriptionId() : null;
        final String securityCode = b2bCSEPaymentForm.getSecurityCode();
        final boolean requestSecurityCode = b2bCSEPaymentForm.isRequestSecurityCode();

        return arePaymentInfoValid(model, subscriptionId, securityCode, requestSecurityCode) &&
                areDeliveryInfoValid(model) &&
                hasTermsAccepted(b2bCSEPaymentForm, model) &&
                isCartValid(model, cartData);
    }

    private boolean isCartValid(final Model model, final CartData cartData) {
        return hasTaxCalculated(cartData, model) &&
                isCartCalculated(cartData, model);
    }

    private boolean areDeliveryInfoValid(final Model model) {
        return hasDeliveryAddress(model) &&
                hasDeliveryMode(model);
    }

    private boolean arePaymentInfoValid(final Model model, final String subscriptionId, final String securityCode, final boolean requestSecurityCode) {
        return isSubscriptionIdInValidCondition(subscriptionId, securityCode, requestSecurityCode, model) &&
                hasRequestedSecurityCode(model, securityCode) &&
                hasPaymentInfo(model);
    }

    private boolean isCartCalculated(final CartData cartData, final Model model) {
        if (!cartData.isCalculated()) {
            LOG.error("Cart {} has a calculated flag of FALSE, placement of order can\'t continue", cartData::getCode);
            addErrorMessage(model, "checkout.error.cart.notcalculated");
            return false;
        }
        return true;
    }

    private boolean hasTaxCalculated(final CartData cartData, final Model model) {
        if (!getCheckoutFacade().containsTaxValues()) {
            LOG.error("Cart {} does not have any tax values, which means the tax calculation was not properly done, placement of order can\'t continue",
                    cartData::getCode);
            addErrorMessage(model, "checkout.error.tax.missing");
            return false;
        }
        return true;
    }

    private boolean hasTermsAccepted(final B2BCSEPaymentForm form, final Model model) {
        if (!form.isTermsCheck()) {
            addErrorMessage(model, "checkout.error.terms.not.accepted");
            return false;
        }
        return true;
    }

    private boolean hasRequestedSecurityCode(final Model model, final String securityCode) {
        if (getRequestSecurityCodeValue() && StringUtils.isBlank(securityCode)) {
            addErrorMessage(model, "checkout.paymentMethod.noSecurityCode");
            return false;
        }
        return true;
    }

    private boolean isSubscriptionIdInValidCondition(final String subscriptionId, final String securityCode, final boolean requestSecurityCode, final Model model) {
        if (subscriptionId != null && StringUtils.isBlank(securityCode) && requestSecurityCode) {
            addErrorMessage(model, "checkout.paymentMethod.noSecurityCode");
            return false;
        }
        return true;
    }

    protected String redirectToOrderConfirmationPage(final PlaceOrderData placeOrderData, final AbstractOrderData orderData) {
        if (Boolean.TRUE.equals(placeOrderData.getNegotiateQuote())) {
            return REDIRECT_URL_QUOTE_ORDER_CONFIRMATION + orderData.getCode();
        } else if (Boolean.TRUE.equals(placeOrderData.getReplenishmentOrder()) && (orderData instanceof ScheduledCartData)) {
            return REDIRECT_URL_REPLENISHMENT_CONFIRMATION + ((ScheduledCartData) orderData).getJobCode();
        }
        return REDIRECT_URL_ORDER_CONFIRMATION + orderData.getCode();
    }

    @Override
    protected CheckoutStep getCheckoutStep() {
        return getCheckoutStep(SUMMARY);
    }

    private WorldpayAdditionalInfoData getWorldpayAdditionalInfo(final HttpServletRequest request,
                                                                 final B2BCSEPaymentForm b2bCSEPaymentForm, final CSEAdditionalAuthInfo cseAdditionalAuthInfo) {
        final WorldpayAdditionalInfoData info = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
        info.setDateOfBirth(b2bCSEPaymentForm.getDateOfBirth());
        info.setSecurityCode(b2bCSEPaymentForm.getSecurityCode());
        info.setDeviceSession(b2bCSEPaymentForm.getDeviceSession());
        if (cseAdditionalAuthInfo.getAdditional3DS2() != null) {
            info.setAdditional3DS2(cseAdditionalAuthInfo.getAdditional3DS2());
        }
        return info;
    }

    protected String getErrorView(final Model model) throws CMSItemNotFoundException {
        setupAddPaymentPage(model);
        addScheduleSetupToModel(model);
        return worldpayAddonEndpointService.getCheckoutSummaryPage();
    }

    protected CheckoutFacade getB2BCheckoutFacade() {
        return (CheckoutFacade) this.getCheckoutFacade();
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

    @InitBinder
    protected void initBinder(final ServletRequestDataBinder binder) {
        final Locale currentLocale = getI18nService().getCurrentLocale();
        final String formatString = getMessageSource().getMessage(TEXT_STORE_DATEFORMAT_KEY, null, DEFAULT_DATEFORMAT,
                currentLocale);
        final DateFormat dateFormat = new SimpleDateFormat(formatString, currentLocale);
        final CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, editor);
    }
}
