package com.worldpay.orderprocess.actions.order;

import com.worldpay.orderprocess.strategies.cancel.WorldpayCancelWholeOrderDueToCancelNotificationStrategy;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;

public class CancelWholeOrderDueToCancelNotificationAction extends AbstractProceduralAction<OrderProcessModel> {

    protected final WorldpayCancelWholeOrderDueToCancelNotificationStrategy worldpayCancelWholeOrderDueToCancelNotificationStrategy;

    public CancelWholeOrderDueToCancelNotificationAction(final WorldpayCancelWholeOrderDueToCancelNotificationStrategy worldpayCancelWholeOrderDueToCancelNotificationStrategy) {
        this.worldpayCancelWholeOrderDueToCancelNotificationStrategy = worldpayCancelWholeOrderDueToCancelNotificationStrategy;
    }

    @Override
    public void executeAction(final OrderProcessModel orderProcessModel) {
        worldpayCancelWholeOrderDueToCancelNotificationStrategy.cancelOrder(orderProcessModel);
    }

}
