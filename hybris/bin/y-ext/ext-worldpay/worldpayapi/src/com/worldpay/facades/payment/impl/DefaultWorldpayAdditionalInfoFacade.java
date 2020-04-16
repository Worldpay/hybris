package com.worldpay.facades.payment.impl;

import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalInfoService;

import javax.servlet.http.HttpServletRequest;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayAdditionalInfoFacade implements WorldpayAdditionalInfoFacade {

    private final WorldpayAdditionalInfoService worldpayAdditionalInfoService;

    public DefaultWorldpayAdditionalInfoFacade(final WorldpayAdditionalInfoService worldpayAdditionalInfoService) {
        this.worldpayAdditionalInfoService = worldpayAdditionalInfoService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayAdditionalInfoData createWorldpayAdditionalInfoData(final HttpServletRequest request) {
        return worldpayAdditionalInfoService.createWorldpayAdditionalInfoData(request);
    }

}
