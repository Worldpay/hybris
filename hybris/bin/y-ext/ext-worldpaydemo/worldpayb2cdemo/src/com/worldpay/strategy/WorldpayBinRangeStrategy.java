package com.worldpay.strategy;

import com.worldpay.model.WorldpayBinRangeModel;

import java.util.List;

/**
 *  Strategy used to select WorldpayBinRangeModel from a list
 */
public interface WorldpayBinRangeStrategy {

    /**
     * Select the bin range to use
     * @param binRanges
     * @return {@link WorldpayBinRangeModel}
     */
    WorldpayBinRangeModel selectBinRange(List<WorldpayBinRangeModel> binRanges);
}
