package com.worldpay.customer;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;

public interface WorldpayCustomerAccountService extends CustomerAccountService {

    /**
     * Delete APM alternative payment method payment info. Before deleting it is checked if the given payment info belongs to the given
     * customer.
     *
     * @param customerModel
     *           the customer model that should be the address owner
     * @param apmPaymentInfo
     *           payment info model that will be deleted
     * @throws IllegalArgumentException
     *            the illegal argument exception is thrown when given payment info does not belong to the given customer
     *            or any argument is null
     */
    void deleteAPMPaymentInfo(final CustomerModel customerModel, final WorldpayAPMPaymentInfoModel apmPaymentInfo);

}
