package com.worldpay.service.payment.impl;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalInfoService;
import com.worldpay.strategy.WorldpayCustomerIpAddressStrategy;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.servlet.http.HttpServletRequest;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.USER_AGENT;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayAdditionalInfoService implements WorldpayAdditionalInfoService {

    private static final String WORLDPAY_ADDITIONAL_DATA_SESSION_ID = "worldpay_additional_data_session_id";

    protected final WorldpayCustomerIpAddressStrategy worldpayCustomerIpAddressStrategy;
    protected final SessionService sessionService;

    public DefaultWorldpayAdditionalInfoService(final WorldpayCustomerIpAddressStrategy worldpayCustomerIpAddressStrategy, final SessionService sessionService) {
        this.worldpayCustomerIpAddressStrategy = worldpayCustomerIpAddressStrategy;
        this.sessionService = sessionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayAdditionalInfoData createWorldpayAdditionalInfoData(final HttpServletRequest request) {
        final WorldpayAdditionalInfoData additionalInfoData = new WorldpayAdditionalInfoData();
        final String sessionId = request.getSession().getId();
        additionalInfoData.setSessionId(sessionId);
        sessionService.setAttribute(WORLDPAY_ADDITIONAL_DATA_SESSION_ID, sessionId);
        additionalInfoData.setCustomerIPAddress(worldpayCustomerIpAddressStrategy.getCustomerIp(request));
        additionalInfoData.setAcceptHeader(request.getHeader(ACCEPT));
        additionalInfoData.setUserAgentHeader(request.getHeader(USER_AGENT));
        return additionalInfoData;
    }
}
