package com.worldpay.service.payment.impl;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.threeds2.RiskData;
import com.worldpay.service.payment.WorldpayRiskDataService;
import de.hybris.platform.core.model.order.CartModel;


/**
 * Default implementation of WorldpayRiskDataService.
 * <p>
 * Methods createAuthenticationRiskData, createShopperAccountRiskData, createTransactionRiskData need
 * to be implemented as they depend on the final solution
 */
public class DefaultWorldpayRiskDataService implements WorldpayRiskDataService {

    /**
     * {@inheritDoc}
     */
    @Override
    public RiskData createRiskData(final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        final RiskData riskData = new RiskData();
        riskData.setAuthenticationRiskData(createAuthenticationRiskData(cartModel, worldpayAdditionalInfoData));
        riskData.setShopperAccountRiskData(createShopperAccountRiskData(cartModel, worldpayAdditionalInfoData));
        riskData.setTransactionRiskData(createTransactionRiskData(cartModel, worldpayAdditionalInfoData));
        return riskData;
    }
}
