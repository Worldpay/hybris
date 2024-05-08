package com.worldpay.worldpayextocc.controllers;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.data.ACHDirectDebitAdditionalAuthInfo;
import com.worldpay.data.Additional3DS2Info;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.dto.BrowserInfoWsDTO;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.dto.payment.AchDirectDebitPaymentWsDTO;
import com.worldpay.enums.AchDirectDebitAccountType;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facade.OCCWorldpayOrderFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.hosted.WorldpayAfterRedirectValidationFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.worldpayextocc.exceptions.NoCheckoutCartException;
import com.worldpay.worldpayextocc.exceptions.ThreeDSecureException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import de.hybris.platform.webservicescommons.validators.CompositeValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
import java.util.Optional;

import static com.worldpay.enums.order.AuthorisedStatus.REFUSED;
import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static com.worldpay.payment.TransactionStatus.ERROR;
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
public class WorldpayOrdersController extends AbstractWorldpayController {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayOrdersController.class);

    private static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";
    private static final String ANONYMOUS_UID = "anonymous";

    // Named like this in order to use the bean definition from commercewebservices
    @Resource(name = "commerceWebServicesCartFacade2")
    private CartFacade cartFacadeCommercewebservices;
    @Resource(name = "cartLoaderStrategy")
    private CartLoaderStrategy cartLoaderStrategy;
    @Resource(name = "worldpayPlaceOrderCartValidator")
    private CompositeValidator worldpayPlaceOrderCartValidator;
    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    private WorldpayCartService worldpayCartService;
    @Resource
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;
    @Resource
    protected WorldpayHostedOrderFacade worldpayHostedOrderFacade;
    @Resource
    protected WorldpayAfterRedirectValidationFacade worldpayAfterRedirectValidationFacade;
    @Resource
    protected OCCWorldpayOrderFacade occWorldpayOrderFacade;
    @Resource (name = "defaultWorldpayUserFacade")
    protected UserFacade worldpayUserFacade;
    @Resource(name = "achDirectDebitPaymentDetailsDTOValidator")
    protected Validator achDirectDebitPaymentDetailsDTOValidator;
    @Resource
    protected WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;
    @Resource
    protected EnumerationService enumerationService;

    /**
     * Authorizes cart and places the order. Response contains the new order data.
     *
     * @param request
     * @param cartId
     * @param securityCode
     * @param fields
     * @return Created order data
     * @throws InvalidCartException
     * @throws NoCheckoutCartException
     * @throws WebserviceValidationException When the cart is not filled properly (e. g. delivery mode is not set, payment method is not set)
     * @formparam cartId Cart code for logged in user, cart GUID for guest checkout
     * @formparam securityCode CCV security code.
     * @queryparam fields Response configuration (list of fields, which should be returned in response)
     * @security Allowed only for customers, customer managers, clients or trusted clients. Trusted client is able to
     * impersonate as any customer and place order on his behalf
     */
    @Secured(
            {"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/users/{userId}/worldpayorders")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiBaseSiteIdAndUserIdParam
    public PlaceOrderResponseWsDTO placeOrder(final HttpServletRequest request,
                                              @RequestParam final String cartId,
                                              @RequestParam final String securityCode,
                                              @RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
                                              @RequestBody final BrowserInfoWsDTO browserInfoWsDTO)
            throws InvalidCartException, NoCheckoutCartException, WorldpayException {

        cartLoaderStrategy.loadCart(cartId);
        validateCartForPlaceOrder();

        //authorize and placeorder
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(request, securityCode, browserInfoWsDTO);
        final DirectResponseData directResponseData = worldpayDirectOrderFacade.authorise(worldpayAdditionalInfoData);

        return handleDirectResponse(directResponseData, fields);
    }

    @Secured(
            {"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/users/{userId}/initial-payment-request")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiBaseSiteIdAndUserIdParam
    public PlaceOrderResponseWsDTO initializePaymentRequest(final HttpServletRequest request,
                                                            @RequestParam final String cartId,
                                                            @RequestParam final String challengeWindowSize,
                                                            @RequestParam final String dfReferenceId,
                                                            @RequestParam final String securityCode,
                                                            @RequestParam final boolean savedCard) throws WorldpayException, InvalidCartException {

        cartLoaderStrategy.loadCart(cartId);

        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = createCseAdditionalAuthInfo(challengeWindowSize, dfReferenceId, savedCard);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(request, securityCode, cseAdditionalAuthInfo, cartId, savedCard);

        final DirectResponseData directResponseData = worldpayDirectOrderFacade.authoriseAndTokenize(worldpayAdditionalInfoData, cseAdditionalAuthInfo);

        return handleDirectResponse(directResponseData, FieldSetLevelHelper.BASIC_LEVEL);
    }


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
            final HttpServletRequest request,
            @RequestParam(defaultValue = FieldSetLevelHelper.FULL_LEVEL) final String fields) throws WorldpayException {

        final Map<String, String> requestParameterMap = getRequestParameterMap(request);
        final RedirectAuthoriseResult redirectAuthoriseResult = occWorldpayOrderFacade
                .getRedirectAuthoriseResult(requestParameterMap);

        if (!requestParameterMap.containsKey(PAYMENT_STATUS_PARAMETER_NAME)) {
            final OrderData orderData = occWorldpayOrderFacade.handleHopResponseWithoutPaymentStatus(redirectAuthoriseResult);
            return dataMapper.map(orderData, OrderWsDTO.class, fields);
        }

        if (worldpayAfterRedirectValidationFacade.validateRedirectResponse(requestParameterMap)) {
            final OrderData orderData = occWorldpayOrderFacade.handleHopResponseWithPaymentStatus(redirectAuthoriseResult);
            return dataMapper.map(orderData, OrderWsDTO.class, fields);
        }

        LOG.error(FAILED_TO_PLACE_ORDER);
        throw new WorldpayException(FAILED_TO_PLACE_ORDER);
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST"})
    @PostMapping(value = "/users/{userId}/carts/{cartId}/worldpayorders/place-banktransfer-redirect-order")
    @Operation(operationId = "PlaceBankTransferRedirectOrder", summary = "Place an order for redirect Bank APMs.", description = "Place the order after APM success redirect. The response contains the new order data.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    @ResponseBody
    public OrderWsDTO placeBankTransferRedirectOrder(
            final HttpServletRequest request,
            @RequestParam(value = "orderId") final String orderId,
            @RequestParam(defaultValue = FieldSetLevelHelper.FULL_LEVEL) final String fields) throws WorldpayException {
        if (!occWorldpayOrderFacade.isValidEncryptedOrderCode(orderId)) {
            LOG.error(FAILED_TO_PLACE_ORDER);
            throw new WorldpayException(FAILED_TO_PLACE_ORDER + " " + REFUSED.name());
        }
        try {
            final Map<String, String> requestParameterMap = getRequestParameterMap(request);
            final RedirectAuthoriseResult redirectAuthoriseResult = occWorldpayOrderFacade.
                    getRedirectAuthoriseResult(requestParameterMap);
            worldpayHostedOrderFacade.completeRedirectAuthorise(redirectAuthoriseResult);

            final OrderData orderData = worldpayCheckoutFacadeDecorator.placeOrder();
            return dataMapper.map(orderData, OrderWsDTO.class, fields);

        } catch (final InvalidCartException e) {
            LOG.error(FAILED_TO_PLACE_ORDER, e);
            throw new WorldpayException(FAILED_TO_PLACE_ORDER);
        }
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
    @RequestMapping(value = "/orders/{orderCode}/user/{userId}", method = RequestMethod.GET)
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

    private CSEAdditionalAuthInfo createCseAdditionalAuthInfo(final String challengeWindowSize, final String dfReferenceId, final Boolean savedCard) {
        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = new CSEAdditionalAuthInfo();
        final Additional3DS2Info additional3DS2 = new Additional3DS2Info();
        additional3DS2.setChallengeWindowSize(challengeWindowSize);
        additional3DS2.setDfReferenceId(dfReferenceId);
        cseAdditionalAuthInfo.setAdditional3DS2(additional3DS2);
        cseAdditionalAuthInfo.setSaveCard(savedCard);
        return cseAdditionalAuthInfo;
    }

    private ACHDirectDebitAdditionalAuthInfo createACHDirectDebitAdditionalAuthInfo(final AchDirectDebitPaymentWsDTO achDirectDebit) {
        final ACHDirectDebitAdditionalAuthInfo achDirectDebitAdditionalAuthInfo = new ACHDirectDebitAdditionalAuthInfo();
        achDirectDebitAdditionalAuthInfo.setAccountNumber(achDirectDebit.getAccountNumber());
        Optional.ofNullable(achDirectDebit.getAccountType())
                .map(String::toUpperCase)
                .map(AchDirectDebitAccountType::valueOf)
                .ifPresent(achDirectDebitAdditionalAuthInfo::setAccountType);
        achDirectDebitAdditionalAuthInfo.setCompanyName(achDirectDebit.getCompanyName());
        achDirectDebitAdditionalAuthInfo.setRoutingNumber(achDirectDebit.getRoutingNumber());
        achDirectDebitAdditionalAuthInfo.setCheckNumber(achDirectDebit.getCheckNumber());
        achDirectDebitAdditionalAuthInfo.setCustomIdentifier(achDirectDebit.getCustomIdentifier());
        achDirectDebitAdditionalAuthInfo.setUsingShippingAsBilling(!worldpayPaymentCheckoutFacade.hasBillingDetails());
        achDirectDebitAdditionalAuthInfo.setSaveCard(Boolean.FALSE);
        achDirectDebitAdditionalAuthInfo.setPaymentMethod(PaymentType.ACHDIRECTDEBITSSL.getMethodCode());
        return achDirectDebitAdditionalAuthInfo;
    }


    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final HttpServletRequest request, final String cvc, final CSEAdditionalAuthInfo cseAdditionalAuthInfo, final String cartId, final boolean savedCard) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfo = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
        worldpayAdditionalInfo.setSecurityCode(cvc);
        worldpayAdditionalInfo.setTransactionIdentifier(cartId);
        worldpayAdditionalInfo.setSavedCardPayment(savedCard);

        if (cseAdditionalAuthInfo.getAdditional3DS2() != null) {
            worldpayAdditionalInfo.setAdditional3DS2(cseAdditionalAuthInfo.getAdditional3DS2());
        }

        return worldpayAdditionalInfo;
    }

    protected void validateCartForPlaceOrder(final String worldPayOrderCode) throws NoCheckoutCartException, InvalidCartException {
        validateCartForPlaceOrder();

        final CartData cartData = checkoutFacade.getCheckoutCart();
        if (!worldPayOrderCode.equals(cartData.getWorldpayOrderCode())) {
            throw new InvalidCartException("Cannot place order. Incorrect worldpay order code");
        }
    }

    protected void validateCartForPlaceOrder() throws NoCheckoutCartException, InvalidCartException {
        if (!checkoutFacade.hasCheckoutCart()) {
            throw new NoCheckoutCartException("Cannot place order. There was no checkout cart created yet!");
        }

        final CartData cartData = checkoutFacade.getCheckoutCart();

        final Errors errors = new BeanPropertyBindingResult(cartData, "sessionCart");
        worldpayPlaceOrderCartValidator.validate(cartData, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }

        try {
            final List<CartModificationData> modificationList = cartFacadeCommercewebservices.validateCartData();
            if (modificationList != null && !modificationList.isEmpty()) {
                final CartModificationDataList cartModificationDataList = new CartModificationDataList();
                cartModificationDataList.setCartModificationList(modificationList);
                throw new WebserviceValidationException(cartModificationDataList);
            }
        } catch (final CommerceCartModificationException e) {
            throw new InvalidCartException(e);
        }
    }

}
