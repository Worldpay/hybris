package com.worldpay.worldpayocccommons.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worldpay.payment.TransactionStatus.AUTHENTICATION_REQUIRED;
import static com.worldpay.payment.TransactionStatus.CANCELLED;
import static org.apache.http.HttpHeaders.USER_AGENT;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.ACHDirectDebitAdditionalAuthInfo;
import com.worldpay.data.Additional3DS2Info;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.dto.BrowserInfoWsDTO;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.dto.order.ThreeDSecureInfoWsDTO;
import com.worldpay.dto.payment.AchDirectDebitPaymentWsDTO;
import com.worldpay.enums.AchDirectDebitAccountType;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.worldpayocccommons.exceptions.NoCheckoutCartException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.validators.CompositeValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Abstract controller with common Worldpay functionality
 */
public class AbstractWorldpayController {

    private static final Logger LOG = Logger.getLogger(AbstractWorldpayController.class);

    protected static final String THREED_SECURE_FLEX_FLOW = "3D-Secure-Flex-Flow";
    protected static final String THREED_SECURE_FLOW = "3D-Secure-Flow";
    protected static final String FAILED_TO_PLACE_ORDER = "Failed to place Order";
    protected static final String DATE_OF_BIRTH_FORMAT = "yyyy-MM-dd";
    protected static final String CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE = "checkout.multi.worldpay.declined.message.";
    protected static final String CHECKOUT_MULTI_WORLDPAY_DECLINED_MESSAGE_DEFAULT = "checkout.multi.worldpay.declined.message.default";

    @Resource
    protected CheckoutFacade checkoutFacade;
    @Resource
    protected WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;
    @Resource(name = "occWorldpayDirectResponseFacade")
    protected WorldpayDirectResponseFacade worldpayDirectResponseFacade;
    @Resource
    protected WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;
    @Resource(name = "defaultWorldpayPlaceOrderCartValidator")
    protected CompositeValidator worldpayPlaceOrderCartValidator;
    @Resource(name = "commerceWebServicesCartFacade2")
    protected CartFacade cartFacadeCommercewebservices;
    @Resource
    protected Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverter;
    @Resource(name = "paymentDetailsDTOValidator")
    protected Validator paymentDetailsDTOValidator;
    @Resource(name = "userFacade")
    protected UserFacade userFacade;
    @Resource(name = "worldpayAddressWsDTOAddressDataPopulator")
    protected Populator<AddressWsDTO, AddressData> worldpayAdressWsDTOAddressDataPopulator;
    @Resource(name = "occWorldpayDirectOrderFacade")
    protected WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource(name = "defaultDataMapper")
    protected DataMapper dataMapper;
    @Resource(name = "worldpayMerchantConfigDataFacade")
    protected WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;
    @Resource(name = "messageSource")
    protected MessageSource messageSource;
    @Resource
    protected I18NService i18nService;

    protected CSEAdditionalAuthInfo createCseAdditionalAuthInfo(final PaymentDetailsWsDTO paymentDetailsWsDTO) {
        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = new CSEAdditionalAuthInfo();
        final Additional3DS2Info additional3DS2 = new Additional3DS2Info();

        additional3DS2.setChallengeWindowSize(paymentDetailsWsDTO.getChallengeWindowSize());
        additional3DS2.setDfReferenceId(paymentDetailsWsDTO.getDfReferenceId());

        cseAdditionalAuthInfo.setAdditional3DS2(additional3DS2);
        cseAdditionalAuthInfo.setEncryptedData(paymentDetailsWsDTO.getCseToken());
        cseAdditionalAuthInfo.setSaveCard(paymentDetailsWsDTO.getSaved() != null ? paymentDetailsWsDTO.getSaved() : Boolean.FALSE);
        cseAdditionalAuthInfo.setUsingShippingAsBilling(false);
        cseAdditionalAuthInfo.setCardHolderName(paymentDetailsWsDTO.getAccountHolderName());
        cseAdditionalAuthInfo.setExpiryYear(paymentDetailsWsDTO.getExpiryYear());
        cseAdditionalAuthInfo.setExpiryMonth(paymentDetailsWsDTO.getExpiryMonth());

        return cseAdditionalAuthInfo;
    }

    protected CSEAdditionalAuthInfo createCSESubscriptionAdditionalAuthInfo(final String challengeWindowSize, final String dfReferenceId) {
        final WorldpayMerchantConfigData currentSiteMerchantConfigData = worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData();
        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = new CSEAdditionalAuthInfo();

        final Additional3DS2Info additional3DS2Info = new Additional3DS2Info();
        additional3DS2Info.setDfReferenceId(dfReferenceId);
        additional3DS2Info.setChallengeWindowSize(challengeWindowSize);
        additional3DS2Info.setChallengePreference(currentSiteMerchantConfigData.getThreeDSFlexChallengePreference());
        cseAdditionalAuthInfo.setAdditional3DS2(additional3DS2Info);
        return cseAdditionalAuthInfo;
    }

    /**
     * Creates a {@link WorldpayAdditionalInfoData} for the given request {@link HttpServletRequest}.
     *
     * @param request the request.
     * @return a {@link WorldpayAdditionalInfoData}
     */
    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final HttpServletRequest request) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfo = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
        worldpayAdditionalInfo.setSessionId(getSessionId(request));
        return worldpayAdditionalInfo;
    }


    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final HttpServletRequest request, final String cvc,
                                                                      final BrowserInfoWsDTO browserInfo) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfo = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
        worldpayAdditionalInfo.setSessionId(getSessionId(request));
        worldpayAdditionalInfo.setSecurityCode(cvc);

        if (browserInfo != null) {
            setBrowserInfo(browserInfo, worldpayAdditionalInfo);
        }
        return worldpayAdditionalInfo;
    }

    protected void setBrowserInfo(final BrowserInfoWsDTO browserInfo, final WorldpayAdditionalInfoData worldpayAdditionalInfo) {
        worldpayAdditionalInfo.setJavaEnabled(browserInfo.getJavaEnabled());
        worldpayAdditionalInfo.setJavascriptEnabled(browserInfo.getJavascriptEnabled());
        worldpayAdditionalInfo.setLanguage(browserInfo.getLanguage());
        worldpayAdditionalInfo.setTimeZone(browserInfo.getTimeZone());
        worldpayAdditionalInfo.setColorDepth(browserInfo.getColorDepth());
        worldpayAdditionalInfo.setScreenHeight(browserInfo.getScreenHeight());
        worldpayAdditionalInfo.setScreenWidth(browserInfo.getScreenWidth());
    }


    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final HttpServletRequest request, final CSEAdditionalAuthInfo cseAdditionalAuthInfo,
                                                                      final String cartId, final BrowserInfoWsDTO browserInfo) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfo = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
        worldpayAdditionalInfo.setSessionId(getSessionId(request));
        worldpayAdditionalInfo.setUserAgentHeader(request.getHeader(USER_AGENT));
        worldpayAdditionalInfo.setTransactionIdentifier(cartId);
        worldpayAdditionalInfo.setSavedCardPayment(cseAdditionalAuthInfo.getSaveCard() != null ? cseAdditionalAuthInfo.getSaveCard() : Boolean.FALSE);

        if (cseAdditionalAuthInfo.getAdditional3DS2() != null) {
            worldpayAdditionalInfo.setAdditional3DS2(cseAdditionalAuthInfo.getAdditional3DS2());
        }

        if (browserInfo != null) {
            setBrowserInfo(browserInfo, worldpayAdditionalInfo);
        }

        return worldpayAdditionalInfo;
    }

    protected String getSessionId(final HttpServletRequest request) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(auth)
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(JwtAuthenticationToken::getToken)
                .map(OAuth2Token::getTokenValue)
                .map(String::hashCode)
                .map(hash -> hash == Integer.MIN_VALUE ? 0 : hash)
                .map(Math::abs)
                .map(String::valueOf)
                .orElse((String) request.getAttribute("jakarta.servlet.request.ssl_session_id"));
    }

    protected PlaceOrderResponseWsDTO handleDirectResponse(final DirectResponseData directResponseData, final String fields) {
        final PlaceOrderResponseWsDTO placeOrderResponseWsDTO = new PlaceOrderResponseWsDTO();

        if (AUTHENTICATION_REQUIRED == directResponseData.getTransactionStatus()) {
            ThreeDSecureInfoWsDTO threeDSecureInfoWsDTO = new ThreeDSecureInfoWsDTO();
            threeDSecureInfoWsDTO.setIssuerURL(directResponseData.getIssuerURL());
            threeDSecureInfoWsDTO.setMerchantData(checkoutFacade.getCheckoutCart().getWorldpayOrderCode());
            threeDSecureInfoWsDTO.setPaRequest(directResponseData.getPaRequest());

            placeOrderResponseWsDTO.setThreeDSecureInfo(threeDSecureInfoWsDTO);
            placeOrderResponseWsDTO.setThreeDSecureNeeded(true);
        } else {
            placeOrderResponseWsDTO.setOrder(dataMapper.map(directResponseData.getOrderData(), OrderWsDTO.class, fields));
            placeOrderResponseWsDTO.setThreeDSecureNeeded(false);
        }

        placeOrderResponseWsDTO.setTransactionStatus(directResponseData.getTransactionStatus());
        placeOrderResponseWsDTO.setReturnCode(directResponseData.getReturnCode());
        placeOrderResponseWsDTO.setReturnMessage(getLocalisedDeclineMessage(directResponseData));

        return placeOrderResponseWsDTO;
    }

    protected PlaceOrderResponseWsDTO handleDirectResponse(final DirectResponseData directResponseData, final HttpServletResponse response, final String fields) throws WorldpayConfigurationException {
        final PlaceOrderResponseWsDTO placeOrderResponseWsDTO = new PlaceOrderResponseWsDTO();

        if (Boolean.TRUE.equals(worldpayDirectResponseFacade.isAuthorised(directResponseData))) {
            placeOrderResponseWsDTO.setOrder(dataMapper.map(directResponseData.getOrderData(), OrderWsDTO.class, fields));
            placeOrderResponseWsDTO.setThreeDSecureNeeded(false);
        }

        if (Boolean.TRUE.equals(worldpayDirectResponseFacade.is3DSecureLegacyFlow(directResponseData))) {
            response.addHeader(THREED_SECURE_FLOW, Boolean.TRUE.toString());
            response.addHeader(THREED_SECURE_FLEX_FLOW, Boolean.FALSE.toString());

            final ThreeDSecureInfoWsDTO threeDSecureInfoWsDTO = new ThreeDSecureInfoWsDTO();
            threeDSecureInfoWsDTO.setIssuerURL(directResponseData.getIssuerURL());
            threeDSecureInfoWsDTO.setMerchantData(checkoutFacade.getCheckoutCart().getWorldpayOrderCode());
            threeDSecureInfoWsDTO.setPaRequest(directResponseData.getPaRequest());

            placeOrderResponseWsDTO.setThreeDSecureInfo(threeDSecureInfoWsDTO);
            placeOrderResponseWsDTO.setThreeDSecureNeeded(true);
        }

        if (Boolean.TRUE.equals(worldpayDirectResponseFacade.is3DSecureFlexFlow(directResponseData))) {
            response.addHeader(THREED_SECURE_FLOW, Boolean.TRUE.toString());
            response.addHeader(THREED_SECURE_FLEX_FLOW, Boolean.TRUE.toString());

            final ThreeDSecureInfoWsDTO threeDSecureInfoWsDTO = new ThreeDSecureInfoWsDTO();
            threeDSecureInfoWsDTO.setIssuerURL(directResponseData.getIssuerURL());
            threeDSecureInfoWsDTO.setMerchantData(checkoutFacade.getCheckoutCart().getWorldpayOrderCode());
            threeDSecureInfoWsDTO.setPaRequest(directResponseData.getPaRequest());
            threeDSecureInfoWsDTO.setThreeDSFlexData(worldpayDirectResponseFacade.retrieveAttributesForFlex3dSecure(directResponseData));

            placeOrderResponseWsDTO.setThreeDSecureInfo(threeDSecureInfoWsDTO);
            placeOrderResponseWsDTO.setThreeDSecureNeeded(true);
        }

        if (Boolean.TRUE.equals(worldpayDirectResponseFacade.isCancelled(directResponseData))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        placeOrderResponseWsDTO.setTransactionStatus(directResponseData.getTransactionStatus());
        placeOrderResponseWsDTO.setReturnCode(directResponseData.getReturnCode());
        placeOrderResponseWsDTO.setReturnMessage(getLocalisedDeclineMessage(directResponseData));

        return placeOrderResponseWsDTO;
    }

    protected String getLocalisedDeclineMessage(final DirectResponseData directResponseData) {
        if (CANCELLED == directResponseData.getTransactionStatus()) {
            return messageSource.getMessage(CHECKOUT_MULTI_WORLDPAY_DECLINED_MESSAGE_DEFAULT, null, i18nService.getCurrentLocale());
        } else if (StringUtils.isNotBlank(directResponseData.getReturnCode())) {
            return messageSource.getMessage(CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE + directResponseData.getReturnCode(), null, i18nService.getCurrentLocale());
        }

        return StringUtils.EMPTY;
    }

    protected Map<String, String> getRequestParameterMap(final HttpServletRequest request) {
        final Map<String, String> map = new HashMap<>();

        final Enumeration<String> myEnum = request.getParameterNames();
        while (myEnum.hasMoreElements()) {
            final String paramName = myEnum.nextElement();
            final String paramValue = request.getParameter(paramName);
            map.put(paramName, paramValue);
        }

        return map;
    }

    protected void validate(final Object object, final String objectName, final Validator validator) {
        final Errors errors = new BeanPropertyBindingResult(object, objectName);
        validator.validate(object, errors);
        if (errors.hasErrors()) {
            throw new WebserviceValidationException(errors);
        }
    }

    protected CSEAdditionalAuthInfo createCseAdditionalAuthInfo(final String challengeWindowSize, final String dfReferenceId, final Boolean savedCard) {
        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = new CSEAdditionalAuthInfo();
        final Additional3DS2Info additional3DS2 = new Additional3DS2Info();
        additional3DS2.setChallengeWindowSize(challengeWindowSize);
        additional3DS2.setDfReferenceId(dfReferenceId);
        cseAdditionalAuthInfo.setAdditional3DS2(additional3DS2);
        cseAdditionalAuthInfo.setSaveCard(savedCard);
        return cseAdditionalAuthInfo;
    }

    protected ACHDirectDebitAdditionalAuthInfo createACHDirectDebitAdditionalAuthInfo(final AchDirectDebitPaymentWsDTO achDirectDebit) {
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

    protected RedirectAuthoriseResult extractAuthoriseResultFromRequest(final Map<String, String> request) {
        return redirectAuthoriseResultConverter.convert(request);
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
     * Convert yyyy-MM-dd string to a LocalDate
     *
     * @param dateString
     * @return
     */
    protected LocalDate convertStringToLocalDate(final String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            LOG.error("failed parsing date of birth", e);
        }
        return null;
    }

    protected CCPaymentInfoData getPaymentInfo(final String paymentDetailsId) {
        LOG.debug("getPaymentInfo : id = " + paymentDetailsId);
        try {
            final CCPaymentInfoData paymentInfoData = userFacade.getCCPaymentInfoForCode(paymentDetailsId);
            if (paymentInfoData == null) {
                throw new RequestParameterException("Payment details [" + paymentDetailsId + "] not found.",
                        RequestParameterException.UNKNOWN_IDENTIFIER, "paymentDetailsId");
            }
            return paymentInfoData;
        } catch (final PK.PKException e) {
            throw new RequestParameterException("Payment details [" + paymentDetailsId + "] not found.",
                    RequestParameterException.UNKNOWN_IDENTIFIER, "paymentDetailsId", e);
        }
    }
}
