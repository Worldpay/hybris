package com.worldpay.service;

import com.worldpay.model.WorldpayBinRangeModel;

/**
 * Service to obtain a bin range from a given card prefix, using a configured strategy
 */
public interface WorldpayBinRangeService {

    /**
     * Get the bin range for the prefix
     * @param prefix
     * @return
     */
    WorldpayBinRangeModel getBinRange(final String prefix);
}
