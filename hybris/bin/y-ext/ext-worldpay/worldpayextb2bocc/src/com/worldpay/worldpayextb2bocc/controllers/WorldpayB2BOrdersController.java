package com.worldpay.worldpayextb2bocc.controllers;


import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.data.ACHDirectDebitAdditionalAuthInfo;
import com.worldpay.dto.BrowserInfoWsDTO;
import com.worldpay.dto.payment.AchDirectDebitPaymentWsDTO;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.impl.WorldpayB2BAcceleratorCheckoutFacadeDecorator;
import com.worldpay.facades.payment.hosted.WorldpayAfterRedirectValidationFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.worldpayocccommons.controllers.AbstractWorldpayController;
import com.worldpay.worldpayocccommons.exceptions.NoCheckoutCartException;
import com.worldpay.worldpayocccommons.exceptions.ThreeDSecureException;
import com.worldpay.worldpayocccommons.facade.OCCWorldpayOrderFacade;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.b2bocc.v2.requestfrom.RequestFromValueSetter;
import de.hybris.platform.b2bocc.v2.skipfield.SkipReplenishmentOrderFieldValueSetter;
import de.hybris.platform.b2bwebservicescommons.dto.order.ReplenishmentOrderWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.order.ScheduleReplenishmentFormWsDTO;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static com.worldpay.payment.TransactionStatus.ERROR;
import static de.hybris.platform.commercefacades.order.constants.OrderOccControllerRequestFromConstants.B2B_ORDERS_CONTROLLER;
import static de.hybris.platform.util.localization.Localization.getLocalizedString;
import static java.text.MessageFormat.format;

/**
 * Web Service Controller for placing Worldpay orders. Methods require authentication
 * and are restricted to https channel.
 *
 * @pathparam code Order GUID (Globally Unique Identifier) or order CODE
 * @pathparam userId User identifier or one of the literals below :
 * <ul>
 * <li>'current' for currently authenticated user</li>
 * <li>'anonymous' for anonymous user</li>
 * </ul>
 */
@Controller
@RequestMapping(value = "/{baseSiteId}")
@SuppressWarnings({"java:S110","common-java:DuplicatedBlocks"})
public class WorldpayB2BOrdersController extends AbstractWorldpayController {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayB2BOrdersController.class);

    private static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";
    private static final String ANONYMOUS_UID = "anonymous";
    private static final String CART_CHECKOUT_TERM_UNCHECKED = "cart.term.unchecked";
    private static final String OBJECT_NAME_SCHEDULE_REPLENISHMENT_FORM = "ScheduleReplenishmentForm";

    // Named like this in order to use the bean definition from commercewebservices
    @Resource
    protected WorldpayHostedOrderFacade worldpayHostedOrderFacade;
    @Resource
    protected WorldpayAfterRedirectValidationFacade worldpayAfterRedirectValidationFacade;
    @Resource(name = "b2bOCCWorldpayOrderFacade")
    protected OCCWorldpayOrderFacade occWorldpayOrderFacade;
    @Resource(name = "achDirectDebitPaymentDetailsDTOValidator")
    protected Validator achDirectDebitPaymentDetailsDTOValidator;
    @Resource
    protected Set<AuthorisedStatus> apmErrorResponseStatuses;
    @Resource(name = "worldpayB2BAcceleratorCheckoutFacadeDecorator")
    protected WorldpayB2BAcceleratorCheckoutFacadeDecorator worldpayB2BCheckoutFacadeDecorator;
    @Resource (name = "defaultWorldpayUserFacade")
    protected UserFacade worldpayUserFacade;
    @Resource(name = "cartLoaderStrategy")
    private CartLoaderStrategy cartLoaderStrategy;
    @Resource
    private WorldpayCartService worldpayCartService;
    @Resource(name = "scheduleReplenishmentFormWsDTOValidator")
    private Validator scheduleReplenishmentFormWsDTOValidator;
    @Resource(name = "skipReplenishmentOrderFieldValueSetter")
    private SkipReplenishmentOrderFieldValueSetter skipReplenishmentOrderFieldValueSetter;
    @Resource(name = "b2bRequestFromValueSetter")
    private RequestFromValueSetter requestFromValueSetter;
    @Resource(name = "b2bCartFacade")
    private CartFacade cartFacade;
    @Resource(name = "b2BPlaceOrderCartValidator")
    private Validator placeOrderCartValidator;

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/users/{userId}/worldpayorders/3dresponse")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiBaseSiteIdAndUserIdParam
    public OrderWsDTO doHandleThreeDSecureResponse(final HttpServletRequest request,
                                                   @RequestParam final String cartId,
                                                   @RequestParam final String paRes,
                                                   @RequestParam final String merchantData,
                                                   @RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
                                                   @RequestBody BrowserInfoWsDTO browserInfo) throws ThreeDSecureException, NoCheckoutCartException, InvalidCartException {

        cartLoaderStrategy.loadCart(cartId);
        validateCartForPlaceOrder(merchantData);

        TransactionStatus transactionStatus = ERROR;
        try {
            final DirectResponseData responseData = worldpayDirectOrderFacade.authorise3DSecure(paRes,
                    createWorldpayAdditionalInfo(request, null, browserInfo));
            transactionStatus = responseData.getTransactionStatus();
            if (AUTHORISED.equals(transactionStatus)) {
                return dataMapper.map(responseData.getOrderData(), OrderWsDTO.class, fields);
            } else {
                LOG.error("Failed to create payment authorisation for successful 3DSecure response. Received {} as transactionStatus", transactionStatus);
                worldpayCartService.setWorldpayDeclineCodeOnCart(merchantData, responseData.getReturnCode());
            }
        } catch (WorldpayException | InvalidCartException e) {
            LOG.error(format("There was an error processing the 3d secure payment for order with worldpayOrderCode [{0}]", merchantData), e);
        }
        throw new ThreeDSecureException(format("Failed to handle authorisation for 3DSecure. Received {0} as transactionStatus", transactionStatus));
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST"})
    @PostMapping(value = "/users/{userId}/carts/{cartId}/worldpayorders/place-redirect-order")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(operationId = "placeRedirectOrder", summary = "Place an order for redirect APMs.", description = "Place the order after APM success redirect. The response contains the new order data.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    @ResponseBody
    public OrderWsDTO placeRedirectOrder(
            @RequestBody final Map<String, String> requestParams,
            @RequestParam(defaultValue = FieldSetLevelHelper.FULL_LEVEL) final String fields) throws WorldpayException, InvalidCartException {

        final String pending = requestParams.get("pending");

        final RedirectAuthoriseResult redirectAuthoriseResult = occWorldpayOrderFacade
                .getRedirectAuthoriseResult(requestParams);

        if (Boolean.FALSE.toString().equals(pending) || Objects.equals(pending, null)) {
            if (!requestParams.containsKey(PAYMENT_STATUS_PARAMETER_NAME)) {
                final OrderData orderData = occWorldpayOrderFacade.handleHopResponseWithoutPaymentStatus(redirectAuthoriseResult);
                return dataMapper.map(orderData, OrderWsDTO.class, fields);
            } else if (worldpayAfterRedirectValidationFacade.validateRedirectResponse(requestParams)) {
                final OrderData orderData = occWorldpayOrderFacade.handleHopResponseWithPaymentStatus(redirectAuthoriseResult);
                return dataMapper.map(orderData, OrderWsDTO.class, fields);
            }

        } else {
            if (worldpayAfterRedirectValidationFacade.validateRedirectResponse(requestParams)) {
                redirectAuthoriseResult.setPending(true);
                final AuthorisedStatus paymentStatus = redirectAuthoriseResult.getPaymentStatus();
                if (!apmErrorResponseStatuses.contains(paymentStatus)) {
                    worldpayHostedOrderFacade.completeRedirectAuthorise(redirectAuthoriseResult);
                    final PlaceOrderData placeOrderData = new PlaceOrderData();
                    placeOrderData.setTermsCheck(Boolean.TRUE);
                    final OrderData orderData = worldpayB2BCheckoutFacadeDecorator.placeOrder(placeOrderData);
                    return dataMapper.map(orderData, OrderWsDTO.class, fields);
                } else {
                    LOG.error("Failed to create payment authorisation for successful pending order. Received status {}", paymentStatus);
                }
            }
        }

        LOG.error(FAILED_TO_PLACE_ORDER);
        throw new WorldpayException(FAILED_TO_PLACE_ORDER);
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST"})
    @PostMapping(value = "/users/{userId}/carts/{cartId}/worldpayorders/place-ach-direct-order")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(operationId = "placeACHDirectOrder", summary = "Place an order for redirect APMs.", description = "Place the order after APM success redirect. The response contains the new order data.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    @ResponseBody
    public OrderWsDTO placeACHDirectOrder(
            final HttpServletRequest request,
            @RequestBody final AchDirectDebitPaymentWsDTO achDirectDebitPayment,
            @RequestParam(defaultValue = FieldSetLevelHelper.FULL_LEVEL) final String fields) throws WorldpayException {

        validate(achDirectDebitPayment, "achDirectDebit", achDirectDebitPaymentDetailsDTOValidator);

        final ACHDirectDebitAdditionalAuthInfo achDirectDebitAdditionalAuthInfo = createACHDirectDebitAdditionalAuthInfo(achDirectDebitPayment);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(request);

        try {

            final OrderData orderData = occWorldpayOrderFacade.handleACHDirectDebitResponse(achDirectDebitAdditionalAuthInfo, worldpayAdditionalInfoData);
            return dataMapper.map(orderData, OrderWsDTO.class, fields);

        } catch (final WorldpayException | InvalidCartException e) {

            LOG.error(FAILED_TO_PLACE_ORDER, e);
            throw new WorldpayException(FAILED_TO_PLACE_ORDER);
        }

    }

    /**
     * Retrieve the order details for a specific order code for the user ID given.
     * This method supports guest users as well.
     * @param userId the user ID
     * @param orderCode the order code
     * @param fields
     * @return the order details
     */
    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST", "ROLE_ANONYMOUS"})
    @GetMapping(value = "/orders/{orderCode}/user/{userId}")
    @ResponseBody
    @Operation(operationId = "getUserOrders", summary = "Get an order.", description = "Returns specific order details based on a specific order code. The response contains detailed order information.")
    @ApiBaseSiteIdParam
    public OrderWsDTO getUserOrders(
            @Parameter(description = "user ID", required = true) @PathVariable final String userId,
            @Parameter(description = "Order code", required = true) @PathVariable final String orderCode,
            @ApiFieldsParam @RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {

        if (!ANONYMOUS_UID.equals(userId) && Boolean.TRUE.equals(worldpayUserFacade.isUserExisting(userId))) {
            final OrderData orderData = occWorldpayOrderFacade.findOrderByCodeAndUserId(orderCode, userId);
            return dataMapper.map(orderData, OrderWsDTO.class, fields);
        }

        final String errInfo = String.format("Could not match any user for uid %s", userId);
        LOG.error(errInfo);
        throw new AccessDeniedException("Access is denied");
    }

    @RequestMappingOverride
    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
                    SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @PostMapping(value = "/orgUsers/{userId}/replenishmentOrders", consumes =
            { MediaType.APPLICATION_JSON_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiBaseSiteIdAndUserIdParam
    @Operation(operationId = "createReplenishmentOrder", summary = "Creates replenishment orders.", description = "Creates and schedules replenishment orders. By default, the payment type is ACCOUNT. Set payment type to CARD if placing an order using credit card.")
    public ReplenishmentOrderWsDTO createReplenishmentOrder(
            @Parameter(description = "Cart identifier: cart code for logged-in user, cart GUID for anonymous user, or 'current' for the last modified cart.", example = "00000110", required = true) @RequestParam(required = true) final String cartId,
            @Parameter(description = "Whether terms were accepted or not.", required = true) @RequestParam final boolean termsChecked,
            @Parameter(description = "Schedule replenishment form object.", required = true) @RequestBody final ScheduleReplenishmentFormWsDTO scheduleReplenishmentForm,
            @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
            throws InvalidCartException {

        validateTerms(termsChecked);

        validateUser();
        skipReplenishmentOrderFieldValueSetter.setValue(fields);
        requestFromValueSetter.setRequestFrom(B2B_ORDERS_CONTROLLER);
        cartLoaderStrategy.loadCart(cartId);
        final CartData cartData = cartFacade.getCurrentCart();

        validateCart(cartData);

        validateScheduleReplenishmentForm(scheduleReplenishmentForm);
        final PlaceOrderData placeOrderData = createPlaceOrderData(scheduleReplenishmentForm);

        return dataMapper.map(worldpayB2BCheckoutFacadeDecorator.placeOrder(placeOrderData), ReplenishmentOrderWsDTO.class, fields);
    }

    protected PlaceOrderData createPlaceOrderData(final ScheduleReplenishmentFormWsDTO scheduleReplenishmentForm) {
        final PlaceOrderData placeOrderData = new PlaceOrderData();
        dataMapper.map(scheduleReplenishmentForm, placeOrderData, false);
        if (scheduleReplenishmentForm != null) {
            placeOrderData.setReplenishmentOrder(Boolean.TRUE);
        }
        placeOrderData.setTermsCheck(Boolean.TRUE);
        return placeOrderData;
    }

    protected void validateUser() {
        if (userFacade.isAnonymousUser()) {
            throw new AccessDeniedException("Access is denied");
        }
    }

    protected void validateTerms(final boolean termsChecked) {
        if (!termsChecked) {
            throw new RequestParameterException(callSuperGetLocalizedString(CART_CHECKOUT_TERM_UNCHECKED));
        }
    }

    protected String callSuperGetLocalizedString(final String key) {
        return getLocalizedString(key);
    }

    protected void validateScheduleReplenishmentForm(final ScheduleReplenishmentFormWsDTO scheduleReplenishmentForm) {
        validate(scheduleReplenishmentForm, OBJECT_NAME_SCHEDULE_REPLENISHMENT_FORM, scheduleReplenishmentFormWsDTOValidator);
    }

    protected void validateCart(final CartData cartData) throws InvalidCartException {
        final Errors errors = new BeanPropertyBindingResult(cartData, "sessionCart");
        placeOrderCartValidator.validate(cartData, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }

        try {
            final List<CartModificationData> modificationList = cartFacade.validateCurrentCartData();
            if (CollectionUtils.isNotEmpty(modificationList)) {
                final CartModificationDataList cartModificationDataList = new CartModificationDataList();
                cartModificationDataList.setCartModificationList(modificationList);
                throw new WebserviceValidationException(cartModificationDataList);
            }
        } catch (final CommerceCartModificationException e) {
            throw new InvalidCartException(e);
        }
    }
}
