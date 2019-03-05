package com.worldpay.controllers;

import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.data.GooglePayAdditionalAuthInfo;
import com.worldpay.dto.order.GooglePayRequestWsDTO;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.exception.WorldpayException;
import com.worldpay.exceptions.NoCheckoutCartException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.populator.options.PaymentDetailsWsDTOOption;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;

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
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
public class WorldpayCartsController extends AbstractWorldpayController {

    private static final Logger LOG = Logger.getLogger(WorldpayCartsController.class);

    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource(name = "userFacade")
    private UserFacade userFacade;
    @Resource(name = "i18NFacade")
    private I18NFacade i18NFacade;
    @Resource(name = "paymentDetailsDTOValidator")
    private Validator paymentDetailsDTOValidator;
    @Resource(name = "dataMapper")
    private DataMapper dataMapper;
    @Resource(name = "checkoutCustomerStrategy")
    private CheckoutCustomerStrategy checkoutCustomerStrategy;
    @Resource
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;
    @Resource(name = "httpRequestPaymentDetailsWsDTOPopulator")
    private ConfigurablePopulator<HttpServletRequest, PaymentDetailsWsDTO, PaymentDetailsWsDTOOption> httpRequestPaymentDetailsWsDTOPopulator;

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
    @ResponseBody
    public PaymentDetailsWsDTO addPaymentDetails(final HttpServletRequest request,
                                                 @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
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
    @ResponseBody
    public PaymentDetailsWsDTO addPaymentDetails(final HttpServletRequest request,
                                                 @RequestBody final PaymentDetailsWsDTO paymentDetails,
                                                 @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
            throws NoCheckoutCartException, WorldpayException {
        return addPaymentDetailsInternal(request, paymentDetails, fields);
    }


    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @PostMapping(value = "/{cartId}/googlepay-details", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlaceOrderResponseWsDTO addPaymentDetailsForGooglePay(@RequestBody final PaymentDetailsWsDTO paymentDetails, @RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
            throws WorldpayException, InvalidCartException {

        final GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfo = createGooglePayAdditionalAuthInfo(paymentDetails.getGooglePayDetails());
        saveBillingAddresses(paymentDetails.getBillingAddress());

        final DirectResponseData responseData = worldpayDirectOrderFacade.authoriseGooglePayDirect(googlePayAdditionalAuthInfo);
        return handleDirectResponse(responseData, fields);
    }

    protected PaymentDetailsWsDTO addPaymentDetailsInternal(final HttpServletRequest request,
                                                            final PaymentDetailsWsDTO paymentDetails,
                                                            final String fields) throws NoCheckoutCartException, WorldpayException {
        validatePayment(paymentDetails);

        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = createCSEAdditionalAuthInfo(paymentDetails);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(request, null);
        try {
            saveBillingAddresses(paymentDetails.getBillingAddress());
            worldpayDirectOrderFacade.tokenize(cseAdditionalAuthInfo, worldpayAdditionalInfoData);
        } catch (final WorldpayException e) {
            LOG.error("There was an error tokenizing the payment details", e);
            throw e;
        }

        final CartData cartData = checkoutFacade.getCheckoutCart();
        final CCPaymentInfoData paymentInfoData = cartData.getPaymentInfo();
        return dataMapper.map(paymentInfoData, PaymentDetailsWsDTO.class, fields);
    }

    private GooglePayAdditionalAuthInfo createGooglePayAdditionalAuthInfo(@RequestBody final GooglePayRequestWsDTO googlePayRequestWsDTO) {
        final GooglePayAdditionalAuthInfo googlePayAdditionalAuthInfo = new GooglePayAdditionalAuthInfo();
        googlePayAdditionalAuthInfo.setProtocolVersion(googlePayRequestWsDTO.getProtocolVersion());
        googlePayAdditionalAuthInfo.setSignature(googlePayRequestWsDTO.getSignature());
        googlePayAdditionalAuthInfo.setSignedMessage(googlePayRequestWsDTO.getSignedMessage());
        return googlePayAdditionalAuthInfo;
    }

    protected CSEAdditionalAuthInfo createCSEAdditionalAuthInfo(final PaymentDetailsWsDTO paymentDetails) {
        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = new CSEAdditionalAuthInfo();
        cseAdditionalAuthInfo.setEncryptedData(paymentDetails.getCseToken());
        cseAdditionalAuthInfo.setCardHolderName(paymentDetails.getAccountHolderName());
        cseAdditionalAuthInfo.setExpiryYear(paymentDetails.getExpiryYear());
        cseAdditionalAuthInfo.setExpiryMonth(paymentDetails.getExpiryMonth());
        cseAdditionalAuthInfo.setSaveCard(paymentDetails.getSaved() != null ? paymentDetails.getSaved() : Boolean.FALSE);
        cseAdditionalAuthInfo.setUsingShippingAsBilling(false);
        return cseAdditionalAuthInfo;
    }

    protected void validatePayment(final PaymentDetailsWsDTO paymentDetails) throws NoCheckoutCartException {
        if (!checkoutFacade.hasCheckoutCart()) {
            throw new NoCheckoutCartException("Cannot add PaymentInfo. There was no checkout cart created yet!");
        }
        validate(paymentDetails, "paymentDetails", paymentDetailsDTOValidator);
    }

    protected void validate(final Object object, final String objectName, final Validator validator) {
        final Errors errors = new BeanPropertyBindingResult(object, objectName);
        validator.validate(object, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
    }

    protected void saveBillingAddresses(final AddressWsDTO address) {
        final AddressData addressData = new AddressData();
        addressData.setId(address.getId());
        addressData.setFirstName(address.getFirstName());
        addressData.setLastName(address.getLastName());
        addressData.setLine1(address.getLine1());
        addressData.setLine2(address.getLine2());
        addressData.setTown(address.getTown());
        addressData.setPostalCode(address.getPostalCode());
        addressData.setCountry(i18NFacade.getCountryForIsocode(address.getCountry().getIsocode()));
        if (address.getRegion() != null) {
            addressData.setRegion(i18NFacade.getRegion(address.getCountry().getIsocode(), address.getRegion().getIsocode()));
        }
        addressData.setPhone(address.getPhone());
        addressData.setShippingAddress(Boolean.TRUE.equals(address.getShippingAddress()));
        addressData.setBillingAddress(Boolean.TRUE);

        addressData.setEmail(checkoutCustomerStrategy.getCurrentUserForCheckout().getContactEmail());
        userFacade.addAddress(addressData);

        worldpayPaymentCheckoutFacade.setBillingDetails(addressData);
    }
}
