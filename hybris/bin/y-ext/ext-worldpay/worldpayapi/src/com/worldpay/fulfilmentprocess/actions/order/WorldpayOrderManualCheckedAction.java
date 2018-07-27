package com.worldpay.fulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Set;

import static de.hybris.platform.core.enums.OrderStatus.FRAUD_CHECKED;

/**
 * Action to verify fraud check, end in undefined if no fraudulent on the order
 */
public class WorldpayOrderManualCheckedAction extends WorldpayAbstractOrderAction<OrderProcessModel> {

    private static final String OK = "OK";
    private static final String NOK = "NOK";
    private static final String UNDEFINED = "UNDEFINED";

    /**
     * Enum with transitions stated when this action is used.
     */
    @Override
    public Set<String> getTransitions() {
        return AbstractAction.createTransitions(OK, NOK, UNDEFINED);
    }

    @Override
    public final String execute(final OrderProcessModel process) {
        ServicesUtil.validateParameterNotNull(process, "Process cannot be null");

        final OrderModel order = process.getOrder();
        ServicesUtil.validateParameterNotNull(order, "Order in process cannot be null");
        if (order.getFraudulent() != null) {
            final OrderHistoryEntryModel historyLog = createHistoryLog(
                    "Order Manually checked by CSA - Fraud = " + order.getFraudulent(), order);
            modelService.save(historyLog);
            if (order.getFraudulent()) {
                setOrderStatus(order, OrderStatus.SUSPENDED);
                removeSavedPaymentInfo(order);
                return NOK;
            }
            setOrderStatus(order, FRAUD_CHECKED);
            return OK;
        }
        return UNDEFINED;
    }

    protected void removeSavedPaymentInfo(final OrderModel order) {
        final PaymentInfoModel originalPaymentInfo = (PaymentInfoModel) order.getPaymentInfo().getOriginal();
        if (originalPaymentInfo != null && originalPaymentInfo.isSaved()) {
            modelService.remove(originalPaymentInfo);
        }
    }
}
