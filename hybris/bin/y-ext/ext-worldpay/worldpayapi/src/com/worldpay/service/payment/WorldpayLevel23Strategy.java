package com.worldpay.service.payment;

import com.worldpay.data.BranchSpecificExtension;
import com.worldpay.data.FraudSightData;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * Strategy to handle the Level 2/3 logic.
 */
public interface WorldpayLevel23Strategy {

    /**
     * Creates the {@link BranchSpecificExtension} based on the abstractOrder details
     *
     * @param abstractOrder the cart
     * @return a populated {@link FraudSightData}
     */
    BranchSpecificExtension createLevel23Data(AbstractOrderModel abstractOrder);

}
