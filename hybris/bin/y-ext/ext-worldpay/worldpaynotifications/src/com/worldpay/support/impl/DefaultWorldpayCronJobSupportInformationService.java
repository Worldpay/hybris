package com.worldpay.support.impl;

import com.worldpay.support.WorldpayCronJobSupportInformationService;
import com.worldpay.worldpaynotifications.model.OrderModificationCronJobModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayCronJobSupportInformationService implements WorldpayCronJobSupportInformationService {

    private FlexibleSearchService flexibleSearchService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<PaymentTransactionType> getPaymentTransactionType() {
        final OrderModificationCronJobModel exampleOrderModificationCronJobModel = new OrderModificationCronJobModel();
        exampleOrderModificationCronJobModel.setActive(true);
        final List<OrderModificationCronJobModel> orderNotificationCronJobModels = flexibleSearchService.getModelsByExample(exampleOrderModificationCronJobModel);

        final Set<PaymentTransactionType> paymentTransactionTypes = new HashSet<>();
        for (final OrderModificationCronJobModel orderNotificationCronJobModel : orderNotificationCronJobModels) {
            paymentTransactionTypes.addAll(orderNotificationCronJobModel.getTypeOfPaymentTransactionToProcessSet());
        }
        return paymentTransactionTypes;
    }

    @Required
    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
