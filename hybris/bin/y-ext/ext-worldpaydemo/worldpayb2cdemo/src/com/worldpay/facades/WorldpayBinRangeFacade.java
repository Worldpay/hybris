package com.worldpay.facades;

import com.worldpay.data.WorldpayBinRangeData;

/**
 * Facade to get WorldpayBinRangeData
 */
public interface WorldpayBinRangeFacade {

    /**
     * Get WorldpayBinRangeData from card prefix
     * @param cardPrefix
     * @return WorldpayBinRangeData
     */
    WorldpayBinRangeData getWorldpayBinRange(final String cardPrefix);
}
