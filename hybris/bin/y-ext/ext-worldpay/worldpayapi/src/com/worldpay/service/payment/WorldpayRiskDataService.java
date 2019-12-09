package com.worldpay.service.payment;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.threeds2.AuthenticationRiskData;
import com.worldpay.service.model.threeds2.RiskData;
import com.worldpay.service.model.threeds2.ShopperAccountRiskData;
import com.worldpay.service.model.threeds2.TransactionRiskData;
import de.hybris.platform.core.model.order.CartModel;

/**
 * Exposes utility methods to create objects used in the request creation about the risk data.
 */
public interface WorldpayRiskDataService {

    /**
     * Creates the risk data object for the initial payment request
     *
     * @param cartModel                  - the current cart
     * @param worldpayAdditionalInfoData - the extendable info data
     * @return RiskData
     */
    RiskData createRiskData(CartModel cartModel, WorldpayAdditionalInfoData worldpayAdditionalInfoData);

    /**
     * Creates the TransactionRiskData for the with risk data information
     *
     * @param cartModel                  - the current cart
     * @param worldpayAdditionalInfoData - the extendable info data
     * @return TransactionRiskData
     */
    default TransactionRiskData createTransactionRiskData(final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        return new TransactionRiskData();
    }

    /**
     * Creates the ShopperAccountRiskData for the with risk data information
     *
     * @param cartModel                  - the current cart
     * @param worldpayAdditionalInfoData - the extendable info data
     * @return ShopperAccountRiskData
     */
    default ShopperAccountRiskData createShopperAccountRiskData(final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        return new ShopperAccountRiskData();
    }

    /**
     * Creates the AuthenticationRiskData for the with risk data information
     *
     * @param cartModel                  - the current cart
     * @param worldpayAdditionalInfoData - the extendable info data
     * @return AuthenticationRiskData
     */
    default AuthenticationRiskData createAuthenticationRiskData(final CartModel cartModel, final WorldpayAdditionalInfoData worldpayAdditionalInfoData) {
        return new AuthenticationRiskData();
    }
}
