package com.worldpay.strategy.impl;

import com.worldpay.model.WorldpayBinRangeModel;
import com.worldpay.strategy.WorldpayBinRangeStrategy;

import java.util.List;

/**
 * {@inheritDoc}
 */
public class GetFirstWorldpayBinRangeStrategy implements WorldpayBinRangeStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayBinRangeModel selectBinRange(final List<WorldpayBinRangeModel> binRanges) {
        return binRanges.isEmpty() ? null : binRanges.get(0);
    }
}
