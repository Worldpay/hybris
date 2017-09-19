package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.exception.WorldpayException;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.hostedorderpage.data.KlarnaRedirectAuthoriseResult;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.service.WorldpayAddonEndpointService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.worldpay.service.model.AuthorisedStatus.*;
import static java.text.MessageFormat.format;

/**
 * Web controller to handle HOP responses
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay")
public class WorldpayHopResponseController extends WorldpayChoosePaymentMethodCheckoutStepController {

    private static final Logger LOG = Logger.getLogger(WorldpayHopResponseController.class);

    protected static final String REDIRECT_URL_ADD_DELIVERY_ADDRESS = REDIRECT_PREFIX + "/checkout/multi/delivery-address/add";
    protected static final String REDIRECT_URL_CHOOSE_DELIVERY_METHOD = REDIRECT_PREFIX + "/checkout/multi/delivery-method/choose";
    protected static final String CHECKOUT_PLACE_ORDER_FAILED = "checkout.placeOrder.failed";
    protected static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";
    protected static final String BILLING_ADDRESS_FORM = "wpBillingAddressForm";
    private static final String KLARNA_RESPONSE_PAGE_DATA_PARAM = "KLARNA_VIEW_DATA";

    @Resource
    private Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverter;
    @Resource
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    @Resource
    private Converter<AbstractOrderModel, OrderData> orderConverter;
    @Resource
    private WorldpayAddonEndpointService worldpayAddonEndpointService;

    @Resource
    private Set<String> apmErrorResponseStatuses;

    /**
     * Handles a successful HOP response
     *
     * @param request            the {@HttpServletRequest} coming from worldpay
     * @param model              the {@Model} to be used
     * @param redirectAttributes the {@RedirectAttributes} to be used
     * @return
     */
    @RequestMapping(value = "/hop-response", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doHandleHopResponse(final HttpServletRequest request, final Model model, final RedirectAttributes redirectAttributes) {
        final Map<String, String> requestParameterMap = getRequestParameterMap(request);
        final RedirectAuthoriseResult response = extractAuthoriseResultFromRequest(request);
        if (!requestParameterMap.containsKey(PAYMENT_STATUS_PARAMETER_NAME)) {
            if (!getCheckoutFacade().hasValidCart()) {
                return checkCart(response);
            }
            try {
                final RedirectAuthoriseResult redirectAuthoriseResult = getWorldpayHostedOrderFacade().inquiryPaymentStatus();
                return processResponse(model, redirectAttributes, redirectAuthoriseResult, redirectAuthoriseResult.getPaymentStatus());
            } catch (WorldpayException e) {
                LOG.error("Error inquiring order in Worldpay", e);
                return doHostedOrderPageError(ERROR.getCode(), redirectAttributes);
            }
        }

        if (getWorldpayHostedOrderFacade().validateRedirectResponse(requestParameterMap)) {
            if (!getCheckoutFacade().hasValidCart()) {
                return checkCart(response);
            }
            return processResponse(model, redirectAttributes, response, response.getPaymentStatus());
        }
        return doHostedOrderPageError(ERROR.getCode(), redirectAttributes);
    }

    private String processResponse(final Model model, final RedirectAttributes redirectAttributes, final RedirectAuthoriseResult response, final String paymentStatus) {
        if (AUTHORISED.getCode().equals(paymentStatus)) {
            getWorldpayHostedOrderFacade().completeRedirectAuthorise(response);
            return placeOrderAndRedirect(model, redirectAttributes);
        } else {
            LOG.error(format("Failed to create payment authorisation for successful hop-response (/hop-response). Received {0}", paymentStatus));
            return doHostedOrderPageError(paymentStatus, redirectAttributes);
        }
    }

    private String checkCart(RedirectAuthoriseResult response) {
        final PaymentTransactionModel paymentTransactionFromCode = worldpayPaymentTransactionService.getPaymentTransactionFromCode(response.getOrderCode());
        if (paymentTransactionFromCode != null && paymentTransactionFromCode.getOrder() instanceof OrderModel) {
            return redirectToOrderConfirmationPage(orderConverter.convert(paymentTransactionFromCode.getOrder()));
        }
        return REDIRECT_URL_CART;
    }

    /**
     * @param request            the {@HttpServletRequest} coming from worldpay
     * @param model              the {@Model} to be used
     * @param redirectAttributes the {@RedirectAttributes} to be used
     * @return
     */
    @RequestMapping(value = "/hop-pending", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doHandlePendingHopResponse(final HttpServletRequest request, final Model model, final RedirectAttributes redirectAttributes) {
        final Map<String, String> requestParameterMap = getRequestParameterMap(request);
        String paymentStatus = ERROR.getCode();
        if (getWorldpayHostedOrderFacade().validateRedirectResponse(requestParameterMap)) {
            final RedirectAuthoriseResult authoriseResult = extractAuthoriseResultFromRequest(request);
            authoriseResult.setPending(true);
            paymentStatus = authoriseResult.getPaymentStatus();
            if (!apmErrorResponseStatuses.contains(paymentStatus)) {
                getWorldpayHostedOrderFacade().completeRedirectAuthorise(authoriseResult);
                return placeOrderAndRedirect(model, redirectAttributes);
            } else {
                LOG.error(format("Failed to create payment authorisation for successful hop-response (/hop-pending). Received {0}", paymentStatus));
            }
        }
        return doHostedOrderPageError(paymentStatus, redirectAttributes);
    }

    /**
     * Handles the non-failing responses from the bank transfer payment methods.
     *
     * @param request            the {@link HttpServletRequest} coming from Worldpay/Bank
     * @param model              the {@Model } to be used
     * @param redirectAttributes the {@RedirectAttributes} to be used
     * @return
     */
    @RequestMapping(value = "bank-transfer/hop-response", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doHandleBankTransferHopResponse(final HttpServletRequest request, final Model model, final RedirectAttributes redirectAttributes) {
        final Map<String, String> requestParameterMap = getRequestParameterMap(request);
        final RedirectAuthoriseResult redirectAuthoriseResult = redirectAuthoriseResultConverter.convert(requestParameterMap);
        getWorldpayHostedOrderFacade().completeRedirectAuthorise(redirectAuthoriseResult);
        return placeOrderAndRedirect(model, redirectAttributes);
    }

    /**
     * Handles the failing responses from the bank transfer payment methods.
     *
     * @param request            the {@link HttpServletRequest} coming from Worldpay/Bank
     * @param redirectAttributes the {@link RedirectAttributes } to be used
     * @return
     */
    @RequestMapping(value = "bank-transfer/hop-failure", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doHandleBankTransferHopFailure(final HttpServletRequest request, final RedirectAttributes redirectAttributes) {
        final Map<String, String> requestParameterMap = getRequestParameterMap(request);
        LOG.info(format("Failed to complete bank transfer for selected payment method. request params {0}", requestParameterMap));

        redirectAttributes.addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, REFUSED.getCode());
        return REDIRECT_URL_CHOOSE_PAYMENT_METHOD;
    }

    /**
     * Do hosted order page error.
     *
     * @param paymentStatus      the payment status
     * @param redirectAttributes the {@link RedirectAttributes } to be used
     * @return url for view
     */
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doHostedOrderPageError(@RequestParam(value = "paymentStatus", required = false) final String paymentStatus, final RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, paymentStatus != null ? paymentStatus : ERROR.getCode());
        return REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=" + paymentStatus;
    }

    /**
     * Do cancel payment.
     *
     * @return the string
     */
    @RequestMapping(value = "/hop-cancel", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doCancelPayment() {
        String redirectUrl = REDIRECT_URL_CHOOSE_PAYMENT_METHOD;
        if (!getCheckoutFacade().hasValidCart()) {
            redirectUrl = REDIRECT_URL_CART;
        } else if (getCheckoutFacade().hasNoDeliveryAddress()) {
            redirectUrl = REDIRECT_URL_ADD_DELIVERY_ADDRESS;
        } else if (getCheckoutFacade().hasNoDeliveryMode()) {
            redirectUrl = REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
        }
        return redirectUrl;
    }

    /**
     * @param countryIsoCode     the country iso code
     * @param useDeliveryAddress the delivery address
     * @param model              the {@link Model} to be used
     * @return
     */
    @RequestMapping(value = "/billingaddressform", method = RequestMethod.GET)
    public String getCountryAddressForm(@RequestParam("countryIsoCode") final String countryIsoCode,
                                        @RequestParam("useDeliveryAddress") final boolean useDeliveryAddress, final Model model) {
        model.addAttribute("supportedCountries", getCountries());
        model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(countryIsoCode));
        model.addAttribute("country", countryIsoCode);

        final PaymentDetailsForm wpPaymentDetailsForm = new PaymentDetailsForm();
        model.addAttribute(BILLING_ADDRESS_FORM, wpPaymentDetailsForm);
        populateAddressForm(countryIsoCode, useDeliveryAddress, wpPaymentDetailsForm);
        return worldpayAddonEndpointService.getBillingAddressForm();
    }

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
            final KlarnaRedirectAuthoriseResult klarnaRedirectAuthoriseResult = getWorldpayPaymentCheckoutFacade().checkKlarnaOrderStatus();
            getWorldpayHostedOrderFacade().completeRedirectAuthorise(klarnaRedirectAuthoriseResult);
            getCheckoutFacade().placeOrder();
            final String decodedHTMLContent = klarnaRedirectAuthoriseResult.getDecodedHTMLContent();
            model.addAttribute(KLARNA_RESPONSE_PAGE_DATA_PARAM, decodedHTMLContent);
            return worldpayAddonEndpointService.getKlarnaResponsePage();
        } catch (WorldpayException | InvalidCartException e) {
            LOG.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, CHECKOUT_PLACE_ORDER_FAILED);
            return doHostedOrderPageError(ERROR.getCode(), redirectAttributes);
        }
    }

    protected RedirectAuthoriseResult extractAuthoriseResultFromRequest(final HttpServletRequest request) {
        final Map<String, String> resultMap = getRequestParameterMap(request);
        return redirectAuthoriseResultConverter.convert(resultMap);
    }

    private String placeOrderAndRedirect(final Model model, final RedirectAttributes redirectAttributes) {
        final OrderData orderData;
        try {
            orderData = getCheckoutFacade().placeOrder();
        } catch (final InvalidCartException e) {
            LOG.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, CHECKOUT_PLACE_ORDER_FAILED);
            return doHostedOrderPageError(ERROR.getCode(), redirectAttributes);
        }
        return redirectToOrderConfirmationPage(orderData);
    }

    /**
     * {@inheritDoc}
     */
    protected Map<String, String> getRequestParameterMap(final HttpServletRequest request) {
        final Enumeration parameterNames = request.getParameterNames();
        final Map<String, String> map = new HashMap<>();
        while (parameterNames.hasMoreElements()) {
            final String paramName = (String) parameterNames.nextElement();
            final String paramValue = request.getParameter(paramName);
            map.put(paramName, paramValue);
        }
        return map;
    }

    protected void populateAddressForm(final String countryIsoCode, final boolean useDeliveryAddress, final PaymentDetailsForm paymentDetailsForm) {
        if (useDeliveryAddress) {
            final AddressData deliveryAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
            final AddressForm addressForm = new AddressForm();

            final RegionData region = deliveryAddress.getRegion();
            if (region != null && !StringUtils.isEmpty(region.getIsocode())) {
                addressForm.setRegionIso(region.getIsocode());
            }
            addressForm.setFirstName(deliveryAddress.getFirstName());
            addressForm.setLastName(deliveryAddress.getLastName());
            addressForm.setLine1(deliveryAddress.getLine1());
            addressForm.setLine2(deliveryAddress.getLine2());
            addressForm.setTownCity(deliveryAddress.getTown());
            addressForm.setPostcode(deliveryAddress.getPostalCode());
            addressForm.setCountryIso(countryIsoCode);
            addressForm.setPhone(deliveryAddress.getPhone());
            paymentDetailsForm.setBillingAddress(addressForm);
        }
    }
}
