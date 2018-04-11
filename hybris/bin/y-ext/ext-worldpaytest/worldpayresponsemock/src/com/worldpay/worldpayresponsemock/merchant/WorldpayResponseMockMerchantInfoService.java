package com.worldpay.worldpayresponsemock.merchant;

import com.worldpay.merchant.WorldpayMerchantInfoService;

import java.util.Set;

/**
 * Mock of Response Merchant Info Service
 */
public interface WorldpayResponseMockMerchantInfoService extends WorldpayMerchantInfoService {

    /**
     * Returns all the merchant codes configured for the environment
     * @return
     */

    Set<String> getAllMerchantCodes();
}
