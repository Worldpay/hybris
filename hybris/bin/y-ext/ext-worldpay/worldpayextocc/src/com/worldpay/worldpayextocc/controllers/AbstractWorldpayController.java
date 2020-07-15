package com.worldpay.worldpayextocc.controllers;

import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.dto.order.ThreeDSecureInfoWsDTO;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.worldpay.payment.TransactionStatus.AUTHENTICATION_REQUIRED;
import static org.apache.http.HttpHeaders.USER_AGENT;

/**
 * Abstract controller with common Worldpay functionality
 */
public class AbstractWorldpayController {

    @Resource
    protected CheckoutFacade checkoutFacade;
    @Resource(name = "dataMapper")
    protected DataMapper dataMapper;
    @Resource
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;

    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final HttpServletRequest request, final String cvc) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfo = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
        worldpayAdditionalInfo.setSessionId(getSessionId(request));
        worldpayAdditionalInfo.setSecurityCode(cvc);
        worldpayAdditionalInfo.setUserAgentHeader(request.getHeader(USER_AGENT));
        return worldpayAdditionalInfo;
    }

    protected String getSessionId(final HttpServletRequest request) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(auth.getDetails())
                .map(OAuth2AuthenticationDetails.class::cast)
                .map(OAuth2AuthenticationDetails::getTokenValue)
                .map(String::hashCode)
                .map(hash -> hash == Integer.MIN_VALUE ? 0 : hash)
                .map(Math::abs)
                .map(abs -> Integer.toString(abs))
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
}
