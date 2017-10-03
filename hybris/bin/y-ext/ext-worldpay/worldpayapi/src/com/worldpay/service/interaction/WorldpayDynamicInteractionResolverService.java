package com.worldpay.service.interaction;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.order.data.WorldpayAdditionalInfoData;

/**
 * Service to determine the dynamic interaction type that performed the order,
 * so we can adapt the shopper interaction based on the method a transaction connects to Worldpay.
 * <p>
 * In direct integration the possible values are:
 * ECOMMERCE and MOTO
 */
public interface WorldpayDynamicInteractionResolverService {

    /**
     * Resolves the interaction type
     *
     * @param worldpayAdditionalInfoData
     * @return {@link DynamicInteractionType}
     */
    DynamicInteractionType resolveInteractionTypeForDirectIntegration(final WorldpayAdditionalInfoData worldpayAdditionalInfoData);
}
