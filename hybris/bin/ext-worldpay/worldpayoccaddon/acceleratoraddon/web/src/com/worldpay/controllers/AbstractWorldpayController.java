package com.worldpay.controllers;

import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Abstract controller with common Worldpay functionality
 */
public class AbstractWorldpayController {

    @Resource
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;

    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final HttpServletRequest request, final String cvc) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfo = worldpayAdditionalInfoFacade.createWorldpayAdditionalInfoData(request);
        worldpayAdditionalInfo.setSessionId(getSessionId());
        worldpayAdditionalInfo.setSecurityCode(cvc);
        return worldpayAdditionalInfo;
    }

    protected String getSessionId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = ((OAuth2AuthenticationDetails) auth.getDetails()).getTokenValue();
        return "" + Math.abs(token.hashCode());
    }

}
