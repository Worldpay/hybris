package com.worldpay.service.interaction.impl;

import com.worldpay.enums.order.DynamicInteractionType;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.interaction.WorldpayDynamicInteractionResolverService;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import org.springframework.beans.factory.annotation.Required;

import static com.worldpay.enums.order.DynamicInteractionType.*;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayDynamicInteractionResolverService implements WorldpayDynamicInteractionResolverService {

    private AssistedServiceService assistedServiceService;

    /**
     * {@inheritDoc}
     */
    @Override
    public DynamicInteractionType resolveInteractionTypeForDirectIntegration(final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        if(worldpayAdditionalInfoData.isReplenishmentOrder()) {
            return CONT_AUTH;
        } else {
            return isASMEnabled() ? MOTO : ECOMMERCE;
        }
    }

    protected boolean isASMEnabled() {
        return assistedServiceService.getAsmSession() != null && assistedServiceService.getAsmSession().getAgent() != null;
    }

    @Required
    public void setAssistedServiceService(final AssistedServiceService assistedServiceService) {
        this.assistedServiceService = assistedServiceService;
    }
}
