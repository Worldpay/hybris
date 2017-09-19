package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.b2bacceleratoraddon.forms.PlaceOrderForm;
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
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.order.InvalidCartException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.addErrorMessage;
import static java.text.MessageFormat.format;

/**
 * Worldpay summary checkout step controller
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay/summary")
public class WorldpaySummaryCheckoutStepController extends AbstractWorldpayDirectCheckoutStepController {

    private static final Logger LOGGER = Logger.getLogger(WorldpaySummaryCheckoutStepController.class);

    protected static final String SUMMARY = "summary";
    protected static final String CART_SUFFIX = "/cart";
    protected static final String REDIRECT_URL_QUOTE_ORDER_CONFIRMATION = REDIRECT_PREFIX + "/checkout/quote/orderConfirmation/";
    protected static final String REDIRECT_URL_REPLENISHMENT_CONFIRMATION = REDIRECT_PREFIX + "/checkout/replenishment/confirmation/";
    protected static final String TEXT_STORE_DATEFORMAT_KEY = "text.store.dateformat";
    protected static final String DEFAULT_DATEFORMAT = "MM/dd/yyyy";
    protected static final int NDAYS_END = 30;
    protected static final int NTHDAYOFMONTH_END = 31;
    protected static final int NTHWEEK_END = 12;

    @Resource
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;
    @Resource
    private UiExperienceService uiExperienceService;
    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    private WorldpayAddonEndpointService worldpayAddonEndpointService;

    /**
     * Enter step
     *
     * @param model
     * @param redirectAttributes
     * @return
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    @PreValidateCheckoutStep(checkoutStep = SUMMARY)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        if (cartData.getEntries() != null && !cartData.getEntries().isEmpty()) {
            for (final OrderEntryData entry : cartData.getEntries()) {
                final String productCode = entry.getProduct().getCode();
                final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode,
                        Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.VARIANT_MATRIX_BASE, ProductOption.PRICE_RANGE));
                entry.setProduct(product);
            }
        }

        model.addAttribute("cartData", cartData);
        model.addAttribute("allItems", cartData.getEntries());
        model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
        model.addAttribute("deliveryMode", cartData.getDeliveryMode());
        model.addAttribute("paymentInfo", cartData.getPaymentInfo());
        addScheduleSetupToModel(model);
        model.addAttribute("requestSecurityCode", getRequestSecurityCodeValue());

        final PlaceOrderForm placeOrderForm = new PlaceOrderForm();
        placeOrderForm.setReplenishmentRecurrence(B2BReplenishmentRecurrenceEnum.MONTHLY);
        placeOrderForm.setnDays("14");
        final List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
        daysOfWeek.add(DayOfWeek.MONDAY);
        placeOrderForm.setnDaysOfWeek(daysOfWeek);
        model.addAttribute("placeOrderForm", placeOrderForm);

        storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        setCheckoutStepLinksForModel(model, getCheckoutStep());
        return worldpayAddonEndpointService.getCheckoutSummaryPage();
    }

    protected void addScheduleSetupToModel(Model model) {
        model.addAttribute("nDays", getNumberRange(1, NDAYS_END));
        model.addAttribute("nthDayOfMonth", getNumberRange(1, NTHDAYOFMONTH_END));
        model.addAttribute("nthWeek", getNumberRange(1, NTHWEEK_END));
        model.addAttribute("daysOfWeek", getB2BCheckoutFacade().getDaysOfWeekForReplenishmentCheckoutSummary());
    }

    protected List<String> getNumberRange(final int startNumber, final int endNumber) {
        final List<String> numbers = new ArrayList<String>();
        for (int number = startNumber; number <= endNumber; number++) {
            numbers.add(String.valueOf(number));
        }
        return numbers;
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
    @RequestMapping(value = "/placeOrder")
    @RequireHardLogIn
    public String placeOrder(@ModelAttribute("placeOrderForm") final PlaceOrderForm placeOrderForm, final Model model, final HttpServletRequest request,
                             final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException, InvalidCartException,
            CommerceCartModificationException {

        if (validateCart(redirectAttributes)) {
            // Invalid cart. Bounce back to the cart page.
            return REDIRECT_PREFIX + CART_SUFFIX;
        }

        if (!isOrderFormValid(placeOrderForm, model)) {
            // redirect to summary
            return enterStep(model, redirectAttributes);
        }

        if (getCheckoutFacade().getCheckoutCart().getPaymentInfo() != null && !placeOrderForm.isReplenishmentOrder()) {
            // Pay by credit card - Place Order Now - authorize payment
            final WorldpayAdditionalInfoData worldpayAdditionalInfoData = getWorldpayAdditionalInfo(request, placeOrderForm.getSecurityCode());
            try {
                final DirectResponseData directResponseData = worldpayDirectOrderFacade.authoriseRecurringPayment(worldpayAdditionalInfoData);
                if (AUTHORISED != directResponseData.getTransactionStatus()) {
                    return handleDirectResponse(model, directResponseData);
                }
            } catch (WorldpayException e) {
                LOGGER.error("There was an error authorising the transaction", e);
                addErrorMessage(model, "checkout.placeOrder.failed");
                return getErrorView(model);
            }
        }

        final PlaceOrderData placeOrderData = new PlaceOrderData();
        placeOrderData.setNDays(placeOrderForm.getnDays());
        placeOrderData.setNDaysOfWeek(placeOrderForm.getnDaysOfWeek());
        placeOrderData.setNthDayOfMonth(placeOrderForm.getNthDayOfMonth());
        placeOrderData.setNWeeks(placeOrderForm.getnWeeks());
        placeOrderData.setReplenishmentOrder(placeOrderForm.isReplenishmentOrder());
        placeOrderData.setReplenishmentRecurrence(placeOrderForm.getReplenishmentRecurrence());
        placeOrderData.setReplenishmentStartDate(placeOrderForm.getReplenishmentStartDate());
        placeOrderData.setSecurityCode(placeOrderForm.getSecurityCode());
        placeOrderData.setTermsCheck(placeOrderForm.isTermsCheck());

        final AbstractOrderData orderData;
        try {
            orderData = getB2BCheckoutFacade().placeOrder(placeOrderData);
        } catch (final EntityValidationException e) {
            LOGGER.error("Failed to place Order", e);
            addErrorMessage(model, e.getLocalizedMessage());

            placeOrderForm.setTermsCheck(false);
            model.addAttribute(placeOrderForm);

            return getErrorView(model);
        } catch (final Exception e) {
            LOGGER.error("Failed to place Order", e);
            addErrorMessage(model, "checkout.placeOrder.failed");
            return getErrorView(model);
        }
        return redirectToOrderConfirmationPage(placeOrderData, orderData);
    }

    protected boolean getRequestSecurityCodeValue() {
        // Only request the security code if using a saved card
        Boolean savedCardSelected = getSessionService().getCurrentSession().getAttribute(SAVED_CARD_SELECTED_ATTRIBUTE);
        return savedCardSelected != null && savedCardSelected.booleanValue();
    }

    /**
     * Validates the order form to filter out invalid order states
     *
     * @param placeOrderForm The spring form of the order being submitted
     * @param model          A spring Model
     * @return True if the order form is invalid and false if everything is valid.
     */
    protected boolean isOrderFormValid(final PlaceOrderForm placeOrderForm, final Model model) {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        final String securityCode = placeOrderForm.getSecurityCode();

        if (getRequestSecurityCodeValue() && StringUtils.isBlank(securityCode)) {
            addErrorMessage(model, "checkout.paymentMethod.noSecurityCode");
            return false;
        }

        if (getCheckoutFlowFacade().hasNoDeliveryAddress()) {
            addErrorMessage(model, "checkout.deliveryAddress.notSelected");
            return false;
        }

        if (getCheckoutFlowFacade().hasNoDeliveryMode()) {
            addErrorMessage(model, "checkout.deliveryMethod.notSelected");
            return false;
        }

        if (getCheckoutFlowFacade().hasNoPaymentInfo()) {
            addErrorMessage(model, "checkout.paymentMethod.notSelected");
            return false;
        }

        if (!placeOrderForm.isTermsCheck()) {
            addErrorMessage(model, "checkout.error.terms.not.accepted");
            return false;
        }

        if (!getCheckoutFacade().containsTaxValues()) {
            LOGGER.error(format(
                    "Cart {0} does not have any tax values, which means the tax calculation was not properly done, placement of order can't continue",
                    cartData.getCode()));
            addErrorMessage(model, "checkout.error.tax.missing");
            return false;
        }

        if (!cartData.isCalculated()) {
            LOGGER.error(format("Cart {0} has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
            addErrorMessage(model, "checkout.error.cart.notcalculated");
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

    /**
     * Navigate to previous step
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/back", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    public String back(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().previousStep();
    }

    /**
     * Navigate to next step
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/next", method = RequestMethod.GET)
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
        info.setUiExperienceLevel(uiExperienceService.getUiExperienceLevel());
        info.setSecurityCode(securityCode);
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
