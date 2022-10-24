package com.worldpay.strategy.impl;

import com.worldpay.consignmentprocess.strategies.AddShippingTrackingInfoToConsignmentStrategy;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * {@inheritDoc}
 */
public class MockAddShippingTrackingInfoToConsignmentStrategy implements AddShippingTrackingInfoToConsignmentStrategy {

    protected final ModelService modelService;

    public MockAddShippingTrackingInfoToConsignmentStrategy(final ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * {@inheritDoc}
     * @param consignment
     */
    @Override
    public void addTrackingInfo(final ConsignmentModel consignment) {
        consignment.setTrackingID(RandomStringUtils.random(5, false, true));
        consignment.setCarrier("MockCarrier");
        modelService.save(consignment);
    }
}
