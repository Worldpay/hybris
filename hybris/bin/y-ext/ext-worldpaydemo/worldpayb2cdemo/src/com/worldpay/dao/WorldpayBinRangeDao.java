package com.worldpay.dao;

import com.worldpay.model.WorldpayBinRangeModel;

import java.util.List;

/**
 * Dao to find worldpay bin ranges
 */
public interface WorldpayBinRangeDao {

    /**
     * Find all possible bin ranges where the @cardPrefix is contained.
     * The dao is not responsible for selecting the correct bin range
     * @param cardPrefix
     * @return list of bin ranges
     */
    List<WorldpayBinRangeModel> findBinRanges(String cardPrefix);

}
