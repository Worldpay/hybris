package com.worldpay.worldpayextocc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.dto.order.WorldpayAPMPaymentInfoWsDTO;
import com.worldpay.dto.payment.PaymentDataWsDTO;
import com.worldpay.dto.payment.PaymentRequestData;
import com.worldpay.enums.AchDirectDebitAccountType;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.APMAvailabilityFacade;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.populator.options.PaymentDetailsWsDTOOption;
import com.worldpay.worldpayextocc.exceptions.NoCheckoutCartException;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.DEFAULT_LEVEL;
import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.FULL_LEVEL;

/**
 * Controller for handling Worldpay specific payments
 *
 * @pathparam userId User identifier or one of the literals below :
 * <ul>
 * <li>'current' for currently authenticated user</li>
 * <li>'anonymous' for anonymous user</li>
 * </ul>
 * @pathparam cartId Cart identifier
 * <ul>
 * <li>cart code for logged in user</li>
 * <li>cart guid for anonymous user</li>
 * <li>'current' for the last modified cart</li>
 * </ul>
 * @security Anonymous user may access cart by its guid. Customer may access only own cart by its id. Trusted client or
 * customer manager may impersonate as any user and access cart on their behalf.
 */
@RestController
@SuppressWarnings("squid:S2387")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
public class WorldpayCartsController extends AbstractWorldpayController {

    private static final Logger LOG = Logger.getLogger(WorldpayCartsController.class);

    private static final String DATE_OF_BIRTH_FORMAT = "yyyy-MM-dd";

    @Resource(name = "occWorldpayDirectOrderFacade")
    protected WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource(name = "userFacade")
    protected UserFacade userFacade;
    @Resource(name = "paymentDetailsDTOValidator")
    protected Validator paymentDetailsDTOValidator;
    @Resource(name = "dataMapper")
    private DataMapper dataMapper;
    @Resource
    protected WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;
    @Resource(name = "httpRequestPaymentDetailsWsDTOPopulator")
    private ConfigurablePopulator<HttpServletRequest, PaymentDetailsWsDTO, PaymentDetailsWsDTOOption> httpRequestPaymentDetailsWsDTOPopulator;
    @Resource(name = "worldpayAddressWsDTOAddressDataPopulator")
    private Populator<AddressWsDTO, AddressData> worldpayAdressWsDTOAddressDataPopulator;
    @Resource(name = "occWorldpayHostedOrderFacade")
    private WorldpayHostedOrderFacade worldpayHostedOrderFacade;
    @Resource
    private APMAvailabilityFacade apmAvailabilityFacade;
    @Resource
    protected WorldpayBankConfigurationFacade worldpayBankConfigurationFacade;
    @Resource
    protected CartFacade cartFacade;
    @Resource
    protected WorldpayPaymentInfoService worldpayPaymentInfoService;
    @Resource
    protected CartService cartService;
    @Resource
    protected EnumerationService enumerationService;
    @Resource
    protected TypeService typeService;


    /**
     * Defines details of a new credit card payment details and assigns the payment to the cart.
     *
     * @param request
     * @param fields
     * @return Created payment details
     * @throws WebserviceValidationException
     * @formparam accountHolderName Name on card. This parameter is required.
     * @formparam cardNumber Card number. This parameter is required.
     * @formparam cardType Card type. This parameter is required. Call GET /{baseSiteId}/cardtypes beforehand to see what
     * card types are supported
     * @formparam expiryMonth Month of expiry date. This parameter is required.
     * @formparam expiryYear Year of expiry date. This parameter is required.
     * @formparam cseToken Client side encrypted card information
     * @formparam issueNumber
     * @formparam startMonth
     * @formparam startYear
     * @formparam subscriptionId
     * @formparam saved Parameter defines if the payment details should be saved for the customer and than could be
     * reused for future orders.
     * @formparam defaultPaymentInfo Parameter defines if the payment details should be used as default for customer.
     * @formparam billingAddress.firstName Customer's first name. This parameter is required.
     * @formparam billingAddress.lastName Customer's last name. This parameter is required.
     * @formparam billingAddress.titleCode Customer's title code. This parameter is required. For a list of codes, see
     * /{baseSiteId}/titles resource
     * @formparam billingAddress.country.isocode Country isocode. This parameter is required and have influence on how
     * rest of address parameters are validated (e.g. if parameters are required :
     * line1,line2,town,postalCode,region.isocode)
     * @formparam billingAddress.line1 First part of address. If this parameter is required depends on country (usually
     * it is required).
     * @formparam billingAddress.line2 Second part of address. If this parameter is required depends on country (usually
     * it is not required)
     * @formparam billingAddress.town Town name. If this parameter is required depends on country (usually it is
     * required)
     * @formparam billingAddress.postalCode Postal code. If this parameter is required depends on country (usually it is
     * required)
     * @formparam billingAddress.region.isocode Isocode for region. If this parameter is required depends on country.
     * @queryparam fields Response configuration (list of fields, which should be returned in response)
     * @security Permitted only for customers, guests, customer managers or trusted clients. Trusted client or customer
     * manager may impersonate as any user and access cart on their behalf.
     */
    @Secured(
            {"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/{cartId}/worldpaypaymentdetails")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDetailsWsDTO addPaymentDetails(final HttpServletRequest request,
                                                 @RequestParam(required = false, defaultValue = FULL_LEVEL) final String fields)
            throws WorldpayException, NoCheckoutCartException {
        final PaymentDetailsWsDTO paymentDetails = new PaymentDetailsWsDTO();
        final Collection<PaymentDetailsWsDTOOption> options = new ArrayList<>();
        options.add(PaymentDetailsWsDTOOption.BASIC);
        options.add(PaymentDetailsWsDTOOption.BILLING_ADDRESS);
        httpRequestPaymentDetailsWsDTOPopulator.populate(request, paymentDetails, options);
        return addPaymentDetailsInternal(request, paymentDetails, fields);
    }

    /**
     * Defines details of a new credit card payment details and assigns the payment to the cart.
     *
     * @param paymentDetails Request body parameter (DTO in xml or json format) which contains details like : Name on card
     *                       (accountHolderName), card number(cardNumber), card type (cardType.code), Month of expiry date
     *                       (expiryMonth), Year of expiry date (expiryYear), if payment details should be saved (saved), if if the
     *                       payment details should be used as default (defaultPaymentInfo), billing address (
     *                       billingAddress.firstName,billingAddress.lastName, billingAddress.titleCode,
     *                       billingAddress.country.isocode, billingAddress.line1, billingAddress.line2, billingAddress.town,
     *                       billingAddress.postalCode, billingAddress.region.isocode),
     *                       Client side encrypted card information (cseToken)
     * @param request
     * @param fields
     * @return Created payment details
     * @throws WebserviceValidationException
     * @throws NoCheckoutCartException
     * @throws WorldpayException
     * @queryparam fields Response configuration (list of fields, which should be returned in response)
     * @bodyparams accountHolderName, cardNumber, cardType, cardTypeData(code), expiryMonth, expiryYear, issueNumber,
     * startMonth, startYear, cseToken
     * ,subscriptionId,defaultPaymentInfo,saved,billingAddress(titleCode,firstName,lastName,line1,line2
     * ,town,postalCode,country(isocode),region(isocode),defaultAddress)
     * @security Permitted only for customers, guests, customer managers or trusted clients. Trusted client or customer
     * manager may impersonate as any user and access cart on their behalf.
     */
    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/{cartId}/worldpaypaymentdetails", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PaymentDetailsWsDTO addPaymentDetails(final HttpServletRequest request,
                                                 @RequestBody final PaymentDetailsWsDTO paymentDetails,
                                                 @RequestParam(required = false, defaultValue = DEFAULT_LEVEL) final String fields)
            throws NoCheckoutCartException, WorldpayException {
        return addPaymentDetailsInternal(request, paymentDetails, fields);
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST"})
    @PostMapping(value = "/{cartId}/place-order")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiBaseSiteIdAndUserIdParam
    public PlaceOrderResponseWsDTO addPaymentDetailsAndPlaceOrder(final HttpServletRequest request,
                                                                  final HttpServletResponse response,
                                                                  @RequestBody final PaymentDetailsWsDTO paymentDetails,
                                                                  @PathVariable final String cartId) throws WorldpayException, InvalidCartException, NoCheckoutCartException {

        validatePayment(paymentDetails);

        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = createCseAdditionalAuthInfo(paymentDetails);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(
                request, cseAdditionalAuthInfo, cartId, paymentDetails.getBrowserInfo());
        worldpayAdditionalInfoData.setDeviceSession(paymentDetails.getDeviceSession());
        Optional.ofNullable(paymentDetails.getDateOfBirth())
                .map(this::convertStringToDate)
                .ifPresent(date -> worldpayAdditionalInfoData.setDateOfBirth(date));

        final DirectResponseData directResponseData = worldpayDirectOrderFacade.executeFirstPaymentAuthorisation3DSecure(cseAdditionalAuthInfo, worldpayAdditionalInfoData);

        return handleDirectResponse(directResponseData, response, FULL_LEVEL);
    }

    /**
     * Endpoint accepts a billing address and assigns it to cart.
     *
     * @param address Request body parameter (DTO in xml or json format) which contains details like :
     *                billing address (
     *                billingAddress.firstName,billingAddress.lastName, billingAddress.titleCode,
     *                billingAddress.country.isocode, billingAddress.line1, billingAddress.line2, billingAddress.town,
     *                billingAddress.postalCode, billingAddress.region.isocode),
     * @param fields
     * @return Created billing address
     * @throws WebserviceValidationException
     * @throws NoCheckoutCartException
     * @throws WorldpayException
     * @queryparam fields Response configuration (list of fields, which should be returned in response)
     * @bodyparams billingAddress(titleCode, firstName, lastName, line1, line2, town, postalCode, country ( isocode), region(isocode), defaultAddress)
     * @security Permitted only for customers, guests, customer managers or trusted clients. Trusted client or customer
     * manager may impersonate as any user and access cart on their behalf.
     */
    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/{cartId}/worldpaybillingaddress", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void addBillingAddressToCart(@RequestBody final AddressWsDTO address,
                                        @RequestParam(required = false, defaultValue = DEFAULT_LEVEL) final String fields) {
        saveBillingAddress(address, fields);
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(operationId = "createAPMPaymentInfo", description = "Creates an APM payment info for the cart.", summary = "Creates a payment info and assigns it to the cart.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    @PostMapping(value = "/{cartId}/worldpayAPMPaymentInfo", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public CartWsDTO createAPMPaymentInfo (@RequestBody final WorldpayAPMPaymentInfoWsDTO apmPaymentInfoWsDto,
                                           @ApiFieldsParam @RequestParam(defaultValue = FULL_LEVEL) final String fields) {

        final CartModel cartModel = cartService.getSessionCart();
        worldpayPaymentInfoService.createAPMPaymentInfo(cartModel, apmPaymentInfoWsDto.getApmCode(), apmPaymentInfoWsDto.getApmName());

        final CartData cartData = cartFacade.getSessionCart();
        cartData.setApmCode(apmPaymentInfoWsDto.getApmCode());
        cartData.setApmName(apmPaymentInfoWsDto.getApmName());

        return dataMapper.map(cartData, CartWsDTO.class, fields);

    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/{cartId}/addresses/worldpaydeliveryaddress", consumes = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(operationId = "createCartDeliveryAndBillingAddress", summary = "Creates a delivery and a payment address for the cart.", description = "Creates an address and assigns it to the cart as the delivery address and the payment address.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public AddressWsDTO createCartDeliveryAndBillingAddress(@Parameter(required = true) @RequestBody final AddressWsDTO address,
                                                            @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_LEVEL) final String fields) {

        final AddressData addressData = dataMapper.map(address, AddressData.class, fields);
        worldpayAdressWsDTOAddressDataPopulator.populate(address, addressData);
        userFacade.addAddress(addressData);
        worldpayPaymentCheckoutFacade.setShippingAndBillingDetails(addressData);

        return dataMapper.map(addressData, AddressWsDTO.class, fields);
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @GetMapping(value = "/{cartId}/payment-method/available")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiBaseSiteIdUserIdAndCartIdParam
    public boolean isPaymentMethodAvailable(@RequestParam final String paymentMethod) {
        return apmAvailabilityFacade.isAvailable(paymentMethod);
    }

    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST",})
    @GetMapping(value = "/{cartId}/payment-method/achdirectdebit/types")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiBaseSiteIdUserIdAndCartIdParam
    public String getACHDirectDebitTypes() throws JsonProcessingException {
        final Map<String,String> map = new HashMap<>();

        enumerationService.getEnumerationValues(AchDirectDebitAccountType._TYPECODE).
                forEach(type -> map.put(type.getCode(), typeService.getEnumerationValue(type).getName()));

        return new ObjectMapper().writeValueAsString(map);
    }

    /**
     * Endpoint for APM redirect authorise
     *
     * @param paymentRequest contains code of the apm, save, cvc
     * @param request
     * @return
     */
    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST"})
    @PostMapping(value = "/{cartId}/payment-method/redirect-authorise")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PaymentDataWsDTO getRedirectAuthorise(@RequestBody final PaymentRequestData paymentRequest,
                                                 final HttpServletRequest request,
                                                 @RequestParam(defaultValue = FieldSetLevelHelper.FULL_LEVEL) final String fields) throws WorldpayException {
        final String paymentMethod = paymentRequest.getPaymentMethod();

        if (!apmAvailabilityFacade.isAvailable(paymentMethod)) {
            throw new IllegalArgumentException("Payment method [" + paymentMethod + "] is not supported");
        }

        final BankTransferAdditionalAuthInfo additionalAuthInfo = new BankTransferAdditionalAuthInfo();
        additionalAuthInfo.setPaymentMethod(paymentMethod);
        additionalAuthInfo.setUsingShippingAsBilling(!worldpayPaymentCheckoutFacade.hasBillingDetails());
        additionalAuthInfo.setSaveCard(paymentRequest.getSave());
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(request);

        if (worldpayBankConfigurationFacade.isBankTransferApm(paymentMethod)) {
            additionalAuthInfo.setShopperBankCode(paymentRequest.getShopperBankCode());
            final PaymentData paymentData = new PaymentData();
            paymentData.setPostUrl(worldpayDirectOrderFacade.authoriseBankTransferRedirect(additionalAuthInfo, worldpayAdditionalInfoData));
            return dataMapper.map(paymentData, PaymentDataWsDTO.class, fields);
        }

        return dataMapper.map(worldpayHostedOrderFacade.redirectAuthorise(additionalAuthInfo, worldpayAdditionalInfoData), PaymentDataWsDTO.class, fields);
    }

    protected PaymentDetailsWsDTO addPaymentDetailsInternal(final HttpServletRequest request,
                                                            final PaymentDetailsWsDTO paymentDetails,
                                                            final String fields) throws NoCheckoutCartException, WorldpayException {
        validatePayment(paymentDetails);

        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = createCseAdditionalAuthInfo(paymentDetails);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(request);
        try {
            saveBillingAddress(paymentDetails.getBillingAddress(), fields);

            worldpayDirectOrderFacade.tokenize(cseAdditionalAuthInfo, worldpayAdditionalInfoData);
        } catch (final WorldpayException e) {
            throw new WorldpayException("There was an error tokenizing the payment details");
        }

        final CartData cartData = checkoutFacade.getCheckoutCart();
        final CCPaymentInfoData paymentInfoData = cartData.getPaymentInfo();

        final PaymentDetailsWsDTO paymentDetailsWsDTO = dataMapper.map(paymentInfoData, PaymentDetailsWsDTO.class, fields);
        paymentDetailsWsDTO.setDefaultPayment(paymentDetails.getDefaultPayment());

        return paymentDetailsWsDTO;
    }


    protected void validatePayment(final PaymentDetailsWsDTO paymentDetails) throws NoCheckoutCartException {
        if (!checkoutFacade.hasCheckoutCart()) {
            throw new NoCheckoutCartException("Cannot add PaymentInfo. There was no checkout cart created yet!");
        }
        validate(paymentDetails, "paymentDetails", paymentDetailsDTOValidator);
    }

    protected void saveBillingAddress(final AddressWsDTO address, final String fields) {
        address.setVisibleInAddressBook(Boolean.FALSE);
        final AddressData addressData = dataMapper.map(address, AddressData.class, fields);
        worldpayAdressWsDTOAddressDataPopulator.populate(address, addressData);
        userFacade.addAddress(addressData);
        worldpayPaymentCheckoutFacade.setBillingDetails(addressData);
    }

    /**
     * Convert yyyy-MM-dd string to a Java Date
     *
     * @param dateString
     * @return
     */
    protected Date convertStringToDate(final String dateString) {
        try {
            return new SimpleDateFormat(DATE_OF_BIRTH_FORMAT).parse(dateString);
        } catch (ParseException e) {
            LOG.error("failed parsing date of birth", e);
        }
        return null;
    }
}
