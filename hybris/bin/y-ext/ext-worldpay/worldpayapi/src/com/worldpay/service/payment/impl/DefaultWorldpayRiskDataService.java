package com.worldpay.service.payment.impl;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.threeds2.RiskData;
import com.worldpay.service.payment.WorldpayRiskDataService;
import de.hybris.platform.core.model.order.CartModel;

public class DefaultWorldpayRiskDataService implements WorldpayRiskDataService {

    /**
     * {@inheritDoc}
     */
    @Override
    public RiskData createRiskData(final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        RiskData riskData = new RiskData();
        riskData.setAuthenticationRiskData(createAuthenticationRiskData(cartModel, worldpayAdditionalInfoData));
        riskData.setShopperAccountRiskData(createShopperAccountRiskData(cartModel, worldpayAdditionalInfoData));
        riskData.setTransactionRiskData(createTransactionRiskData(cartModel, worldpayAdditionalInfoData));
        return riskData;
    }
}
