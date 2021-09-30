package com.worldpay.converters.populators;

import com.worldpay.model.WorldpayFraudSightModel;
import com.worldpay.data.FraudSightResponse;
import de.hybris.platform.converters.Populator;

/**
 * Populator to transform a {@link FraudSightResponse} received from Worldpay to a {@link WorldpayFraudSightModel}
 */
public class WorldpayFraudSightResponsePopulator implements Populator<FraudSightResponse, WorldpayFraudSightModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final FraudSightResponse fraudSightResponse, final WorldpayFraudSightModel worldpayFraudSight) {
        worldpayFraudSight.setId(fraudSightResponse.getId());
        worldpayFraudSight.setScore(fraudSightResponse.getScore());
        worldpayFraudSight.setMessage(fraudSightResponse.getMessage());

        worldpayFraudSight.setReasonCodes(fraudSightResponse.getReasonCodes());
    }
}
