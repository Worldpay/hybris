package com.worldpay.consignmentprocess.strategies.consigment.impl;

import com.worldpay.consignmentprocess.strategies.AddShippingTrackingInfoToConsignmentStrategy;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

/**
 *  Void implementation for adding tracking info to the consignment
 */
public class DefaultWorldpayAddShippingTrackingInfoToConsignmentStrategy implements AddShippingTrackingInfoToConsignmentStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTrackingInfo(final ConsignmentModel consignment) {
        //add here your own implementation
    }
}
