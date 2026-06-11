package com.worldpay.strategy.impl;

import com.worldpay.strategy.WorldpayCustomerIpAddressStrategy;
import org.apache.commons.lang3.StringUtils;


import jakarta.servlet.http.HttpServletRequest;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayCustomerIpAddressStrategy implements WorldpayCustomerIpAddressStrategy {

    protected final String headerName;

    public DefaultWorldpayCustomerIpAddressStrategy(final String headerName) {
        this.headerName = headerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustomerIp(final HttpServletRequest request) {
        final String headerValue = request.getHeader(headerName);
        return containsValidIp(headerValue) ? request.getRemoteAddr() : headerValue;
    }

    private boolean containsValidIp(final String headerValue) {
        return StringUtils.isBlank(headerValue) || "unknown".equalsIgnoreCase(headerValue);
    }

}
