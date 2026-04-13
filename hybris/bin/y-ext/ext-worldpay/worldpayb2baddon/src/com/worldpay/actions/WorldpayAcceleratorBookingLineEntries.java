/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.worldpay.actions;

import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.process.approval.actions.SetBookingLineEntries;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.SAPGenericPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.task.RetryLaterException;

/**
 * The AcceleratorBookingLineEntries.
 */
public class WorldpayAcceleratorBookingLineEntries extends SetBookingLineEntries {
    @Override
    public Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException {
        final OrderModel order = process.getOrder();
        modelService.refresh(order);
        final PaymentInfoModel paymentInfo = order.getPaymentInfo();

        if (CheckoutPaymentType.CARD.equals(order.getPaymentType())
                && (paymentInfo instanceof CreditCardPaymentInfoModel ||
                Boolean.TRUE.equals(paymentInfo.getIsApm()) ||
                paymentInfo instanceof SAPGenericPaymentInfoModel)) {
            return Transition.OK;
        } else {
            return super.executeAction(process);
        }
    }
}
