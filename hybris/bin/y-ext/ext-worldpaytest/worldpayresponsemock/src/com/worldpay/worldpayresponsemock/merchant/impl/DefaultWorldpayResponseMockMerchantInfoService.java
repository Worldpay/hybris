package com.worldpay.worldpayresponsemock.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.merchant.impl.DefaultWorldpayMerchantInfoService;
import com.worldpay.worldpayresponsemock.merchant.WorldpayResponseMockMerchantInfoService;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayResponseMockMerchantInfoService extends DefaultWorldpayMerchantInfoService implements WorldpayResponseMockMerchantInfoService {

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAllMerchantCodes() {
        return getConfiguredMerchants().stream().map(WorldpayMerchantConfigData::getCode).collect(toSet());
    }
}
