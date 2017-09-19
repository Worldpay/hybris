package com.worldpay.worldpayresponsemock.merchant;

import com.worldpay.merchant.WorldpayMerchantInfoService;

import java.util.Set;

/**
 * Mock of Response Merchant Info Service
 */
public interface WorldpayResponseMockMerchantInfoService extends WorldpayMerchantInfoService {

    /**
     * Returns all the merchant codes configured for the environment
     * @param siteUid The site uid the merchants are configured for
     * @return
     */

    Set<String> getAllMerchantCodes(final String siteUid);
}
