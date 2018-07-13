package com.worldpay.controllers;

import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.apache.http.HttpHeaders.USER_AGENT;

/**
 * Abstract controller with common Worldpay functionality
 */
public class AbstractWorldpayController {

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

}
