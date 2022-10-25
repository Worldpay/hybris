package com.worldpay.facades.impl;

import com.worldpay.converters.WorldpayBinRangeConverter;
import com.worldpay.data.WorldpayBinRangeData;
import com.worldpay.facades.WorldpayBinRangeFacade;
import com.worldpay.model.WorldpayBinRangeModel;
import com.worldpay.service.WorldpayBinRangeService;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayBinRangeFacade implements WorldpayBinRangeFacade {

    private WorldpayBinRangeService worldpayBinRangeService;

    private WorldpayBinRangeConverter worldpayBinRangeConverter;

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayBinRangeData getWorldpayBinRange(final String cardPrefix) {
        WorldpayBinRangeModel model = worldpayBinRangeService.getBinRange(cardPrefix);
        WorldpayBinRangeData worldpayBinRangeData = null;

        if (model != null) {
            worldpayBinRangeData = worldpayBinRangeConverter.convert(model);
        }

        return worldpayBinRangeData;
    }

    @Required
    public void setWorldpayBinRangeService(WorldpayBinRangeService worldpayBinRangeService) {
        this.worldpayBinRangeService = worldpayBinRangeService;
    }

    @Required
    public void setWorldpayBinRangeConverter(WorldpayBinRangeConverter worldpayBinRangeConverter) {
        this.worldpayBinRangeConverter = worldpayBinRangeConverter;
    }
}
