package com.worldpay.consignmentprocess.actions.consigment;

import com.worldpay.consignmentprocess.strategies.AddShippingTrackingInfoToConsignmentStrategy;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;

/**
 * Action for adding the shipping tracking information to the consignment
 */
public class AddShippingTrackingInfoAction extends AbstractProceduralAction<ConsignmentProcessModel> {

    private final AddShippingTrackingInfoToConsignmentStrategy addShippingTrackingInfoToConsignmentStrategy;

    /**
     * @param addShippingTrackingInfoToConsignmentStrategy
     */
    public AddShippingTrackingInfoAction(final AddShippingTrackingInfoToConsignmentStrategy addShippingTrackingInfoToConsignmentStrategy) {
        this.addShippingTrackingInfoToConsignmentStrategy = addShippingTrackingInfoToConsignmentStrategy;
    }

    @Override
    public void executeAction(final ConsignmentProcessModel consignmentProcessModel) throws RetryLaterException, Exception {
        addShippingTrackingInfoToConsignmentStrategy.addTrackingInfo(consignmentProcessModel.getConsignment());
    }
}
