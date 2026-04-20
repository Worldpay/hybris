package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.facades.payment.hosted.WorldpayAfterRedirectValidationFacade;
import com.worldpay.facades.payment.hosted.WorldpayHOPNoReturnParamsStrategy;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;
import static com.worldpay.enums.order.AuthorisedStatus.ERROR;

/**
 * Web controller to handle HOP responses
 */
@Controller
@RequestMapping(value = "/checkout/multi/worldpay")
@SuppressWarnings({"java:S110","common-java:DuplicatedBlocks"})
public class WorldpayB2BResponseController extends WorldpayChoosePaymentMethodCheckoutStepController {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayB2BResponseController.class);

    private static final String REDIRECT_URL_ADD_DELIVERY_ADDRESS = REDIRECT_PREFIX + "/checkout/multi/delivery-address/add";
    private static final String REDIRECT_URL_CHOOSE_DELIVERY_METHOD = REDIRECT_PREFIX + "/checkout/multi/delivery-method/choose";
    private static final String CHECKOUT_PLACE_ORDER_FAILED = "checkout.placeOrder.failed";
    private static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";

    @Resource
    protected Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverter;
    @Resource
    protected WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    @Resource
    protected Converter<AbstractOrderModel, OrderData> orderConverter;
    @Resource
    protected Set<AuthorisedStatus> apmErrorResponseStatuses;
    @Resource
    protected WorldpayHOPNoReturnParamsStrategy worldpayHOPNoReturnParamsStrategy;
    @Resource
    protected WorldpayHostedOrderFacade worldpayHostedOrderFacade;
    @Resource
    protected WorldpayAfterRedirectValidationFacade worldpayAfterRedirectValidationFacade;

    /**
     * Handles a successful HOP response
     *
     * @param request            the {@link HttpServletRequest} coming from worldpay
     * @param model              the {@link Model} to be used
     * @param redirectAttributes the {@link RedirectAttributes} to be used
     * @return
     */
    @RequireHardLogIn
    @GetMapping(value = "/hop-response")
    public String doHandleHopResponse(final HttpServletRequest request, final Model model, final RedirectAttributes redirectAttributes) {
        final Map<String, String> requestParameterMap = getRequestParameterMap(request);
        final RedirectAuthoriseResult response = extractAuthoriseResultFromRequest(request);
        if (!requestParameterMap.containsKey(PAYMENT_STATUS_PARAMETER_NAME)) {
            return handleHopResponseWithoutPaymentStatus(model, redirectAttributes, response);
        }

        if (worldpayAfterRedirectValidationFacade.validateRedirectResponse(requestParameterMap)) {
            return handleHopResponseWithPaymentStatus(model, redirectAttributes, response);
        }
        return doHostedOrderPageError(ERROR.name(), redirectAttributes);
    }

    /**
     * @param request            the {@HttpServletRequest} coming from worldpay
     * @param model              the {@Model} to be used
     * @param redirectAttributes the {@RedirectAttributes} to be used
     * @return
     */
    @RequireHardLogIn
    @GetMapping(value = "/hop-pending")
    public String doHandlePendingHopResponse(final HttpServletRequest request, final Model model, final RedirectAttributes redirectAttributes) {
        final Map<String, String> requestParameterMap = getRequestParameterMap(request);
        AuthorisedStatus paymentStatus = AuthorisedStatus.ERROR;
        if (worldpayAfterRedirectValidationFacade.validateRedirectResponse(requestParameterMap)) {
            final RedirectAuthoriseResult authoriseResult = extractAuthoriseResultFromRequest(request);
            authoriseResult.setPending(true);
            paymentStatus = authoriseResult.getPaymentStatus();
            if (!apmErrorResponseStatuses.contains(paymentStatus)) {
                worldpayHostedOrderFacade.completeRedirectAuthorise(authoriseResult);
                return placeOrderAndRedirect(model, redirectAttributes);
            } else {
                LOG.error("Failed to create payment authorisation for successful hop-response (/hop-pending). Received {}", paymentStatus);
            }
        }
        return doHostedOrderPageError(paymentStatus.name(), redirectAttributes);
    }

    /**
     * Do cancel payment.
     *
     * @return the string
     */
    @GetMapping(value = "/hop-cancel")
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
     * Do hosted order page error.
     *
     * @param paymentStatus      the payment status
     * @param redirectAttributes the {@link RedirectAttributes } to be used
     * @return url for view
     */
    @GetMapping(value = "/error")
    @RequireHardLogIn
    public String doHostedOrderPageError(@RequestParam(value = "paymentStatus", required = false) final String paymentStatus, final RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, paymentStatus != null ? paymentStatus : AuthorisedStatus.ERROR.name());
        return REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=" + paymentStatus;
    }

    /**
     * @param countryIsoCode     the country iso code
     * @param useDeliveryAddress the delivery address
     * @param model              the {@link Model} to be used
     * @return the address form
     */
    @GetMapping(value = "/billingaddressform")
    public String getCountryAddressForm(@RequestParam("countryIsoCode") final String countryIsoCode,
                                        @RequestParam("useDeliveryAddress") final boolean useDeliveryAddress, final Model model) {
        return super.getCountryAddressForm(countryIsoCode, useDeliveryAddress, model);
    }

    protected String handleHopResponseWithPaymentStatus(final Model model, final RedirectAttributes redirectAttributes, final RedirectAuthoriseResult response) {
        if (!getCheckoutFacade().hasValidCart()) {
            return checkCart(response);
        }
        return processResponse(model, redirectAttributes, response, response.getPaymentStatus());
    }

    protected String handleHopResponseWithoutPaymentStatus(final Model model, final RedirectAttributes redirectAttributes, final RedirectAuthoriseResult response) {
        if (!getCheckoutFacade().hasValidCart()) {
            return checkCart(response);
        }
        final RedirectAuthoriseResult redirectAuthoriseResult = worldpayHOPNoReturnParamsStrategy.authoriseCart();
        return processResponse(model, redirectAttributes, redirectAuthoriseResult, redirectAuthoriseResult.getPaymentStatus());
    }

    protected String processResponse(final Model model, final RedirectAttributes redirectAttributes, final RedirectAuthoriseResult response, final AuthorisedStatus paymentStatus) {
        if (AUTHORISED.equals(paymentStatus)) {
            worldpayHostedOrderFacade.completeRedirectAuthorise(response);
            return placeOrderAndRedirect(model, redirectAttributes);
        } else {
            final String paymentStatusName = paymentStatus.name();
            LOG.error("Failed to create payment authorisation for successful hop-response (/hop-response). Received {}", paymentStatusName);
            return doHostedOrderPageError(paymentStatusName, redirectAttributes);
        }
    }

    protected String checkCart(final RedirectAuthoriseResult response) {
        final PaymentTransactionModel paymentTransactionFromCode = worldpayPaymentTransactionService.getPaymentTransactionFromCode(response.getOrderCode());
        if (paymentTransactionFromCode != null && paymentTransactionFromCode.getOrder() instanceof OrderModel) {
            return redirectToOrderConfirmationPage(orderConverter.convert(paymentTransactionFromCode.getOrder()));
        }
        return REDIRECT_URL_CART;
    }

    protected RedirectAuthoriseResult extractAuthoriseResultFromRequest(final HttpServletRequest request) {
        final Map<String, String> resultMap = getRequestParameterMap(request);
        return redirectAuthoriseResultConverter.convert(resultMap);
    }

    protected String placeOrderAndRedirect(final Model model, final RedirectAttributes redirectAttributes) {
        final OrderData orderData;
        try {
            final PlaceOrderData placeOrderData = new PlaceOrderData();
            placeOrderData.setTermsCheck(Boolean.TRUE);
            orderData = getB2BCheckoutFacade().placeOrder(placeOrderData);
        } catch (final InvalidCartException e) {
            LOG.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, CHECKOUT_PLACE_ORDER_FAILED);
            return doHostedOrderPageError(ERROR.name(), redirectAttributes);
        }
        return redirectToOrderConfirmationPage(orderData);
    }

    protected CheckoutFacade getB2BCheckoutFacade() {
        return (CheckoutFacade) this.getCheckoutFacade();
    }
}
