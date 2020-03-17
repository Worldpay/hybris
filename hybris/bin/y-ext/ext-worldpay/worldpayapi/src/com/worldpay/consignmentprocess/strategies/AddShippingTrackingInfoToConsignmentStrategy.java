package com.worldpay.consignmentprocess.strategies;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;

/**
 * Strategy responsible of adding the tracking info to a consignment
 */
public interface AddShippingTrackingInfoToConsignmentStrategy {

    /**
     * Adds the {@link ConsignmentModel#TRACKINGID} for the given {@link ConsignmentModel}
     *
     * @param consignment
     *     the {@link ConsignmentModel} for which tracking id needs to be generated
     */
    void addTrackingInfo(final ConsignmentModel consignment);
}
