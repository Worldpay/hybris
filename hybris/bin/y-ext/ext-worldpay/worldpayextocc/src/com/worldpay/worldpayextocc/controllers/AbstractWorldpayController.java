package com.worldpay.worldpayextocc.controllers;

import com.worldpay.data.Additional3DS2Info;
import com.worldpay.data.Browser;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.dto.BrowserInfoWsDTO;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.dto.order.ThreeDSecureInfoWsDTO;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.worldpay.payment.TransactionStatus.AUTHENTICATION_REQUIRED;
import static org.apache.http.HttpHeaders.USER_AGENT;

/**
 * Abstract controller with common Worldpay functionality
 */
public class AbstractWorldpayController {

    protected static final String THREED_SECURE_FLEX_FLOW = "3D-Secure-Flex-Flow";
    protected static final String THREED_SECURE_FLOW = "3D-Secure-Flow";
    protected static final String FAILED_TO_PLACE_ORDER = "Failed to place Order";

    @Resource
    protected CheckoutFacade checkoutFacade;
    @Resource(name = "dataMapper")
    protected DataMapper dataMapper;
    @Resource
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;
    @Resource(name = "occWorldpayDirectResponseFacade")
    protected WorldpayDirectResponseFacade worldpayDirectResponseFacade;
    @Resource
    protected WorldpayCheckoutFacadeDecorator worldpayCheckoutFacadeDecorator;

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

        if(browserInfo != null) {
            setBrowserInfo(browserInfo, worldpayAdditionalInfo);
        }

        return worldpayAdditionalInfo;
    }

    protected String getSessionId(final HttpServletRequest request) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(auth)
            .map(Authentication::getDetails)
            .map(OAuth2AuthenticationDetails.class::cast)
            .map(OAuth2AuthenticationDetails::getTokenValue)
            .map(String::hashCode)
            .map(hash -> hash == Integer.MIN_VALUE ? 0 : hash)
            .map(Math::abs)
            .map(String::valueOf)
            .orElse((String) request.getAttribute("javax.servlet.request.ssl_session_id"));
    }

    protected PlaceOrderResponseWsDTO handleDirectResponse(final DirectResponseData directResponseData, final String fields) {
        PlaceOrderResponseWsDTO placeOrderResponseWsDTO = new PlaceOrderResponseWsDTO();

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

        return placeOrderResponseWsDTO;
    }

    protected PlaceOrderResponseWsDTO handleDirectResponse(final DirectResponseData directResponseData, final HttpServletResponse response, final String fields) throws WorldpayConfigurationException {
        final PlaceOrderResponseWsDTO placeOrderResponseWsDTO = new PlaceOrderResponseWsDTO();

        if (worldpayDirectResponseFacade.isAuthorised(directResponseData)) {
            placeOrderResponseWsDTO.setOrder(dataMapper.map(directResponseData.getOrderData(), OrderWsDTO.class, fields));
            placeOrderResponseWsDTO.setThreeDSecureNeeded(false);
        }

        if (worldpayDirectResponseFacade.is3DSecureLegacyFlow(directResponseData)) {
            response.addHeader(THREED_SECURE_FLOW, Boolean.TRUE.toString());
            response.addHeader(THREED_SECURE_FLEX_FLOW, Boolean.FALSE.toString());

            final ThreeDSecureInfoWsDTO threeDSecureInfoWsDTO = new ThreeDSecureInfoWsDTO();
            threeDSecureInfoWsDTO.setIssuerURL(directResponseData.getIssuerURL());
            threeDSecureInfoWsDTO.setMerchantData(checkoutFacade.getCheckoutCart().getWorldpayOrderCode());
            threeDSecureInfoWsDTO.setPaRequest(directResponseData.getPaRequest());

            placeOrderResponseWsDTO.setThreeDSecureInfo(threeDSecureInfoWsDTO);
            placeOrderResponseWsDTO.setThreeDSecureNeeded(true);
        }

        if (worldpayDirectResponseFacade.is3DSecureFlexFlow(directResponseData)) {
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

        if (worldpayDirectResponseFacade.isCancelled(directResponseData)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        placeOrderResponseWsDTO.setTransactionStatus(directResponseData.getTransactionStatus());
        placeOrderResponseWsDTO.setReturnCode(directResponseData.getReturnCode());

        return placeOrderResponseWsDTO;
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
}
