package com.worldpay.service.impl;

import com.worldpay.dao.WorldpayBinRangeDao;
import com.worldpay.model.WorldpayBinRangeModel;
import com.worldpay.service.WorldpayBinRangeService;
import com.worldpay.strategy.WorldpayBinRangeStrategy;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayBinRangeService implements WorldpayBinRangeService {

    private WorldpayBinRangeStrategy worldpayBinRangeStrategy;

    private WorldpayBinRangeDao worldpayBinRangeDao;


    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayBinRangeModel getBinRange(final String prefix) {
        List<WorldpayBinRangeModel> binRanges = worldpayBinRangeDao.findBinRanges(prefix);
        return worldpayBinRangeStrategy.selectBinRange(binRanges);
    }

    @Required
    public void setWorldpayBinRangeStrategy(final WorldpayBinRangeStrategy worldpayBinRangeStrategy) {
        this.worldpayBinRangeStrategy = worldpayBinRangeStrategy;
    }

    @Required
    public void setWorldpayBinRangeDao(final WorldpayBinRangeDao worldpayBinRangeDao) {
        this.worldpayBinRangeDao = worldpayBinRangeDao;
    }
}
