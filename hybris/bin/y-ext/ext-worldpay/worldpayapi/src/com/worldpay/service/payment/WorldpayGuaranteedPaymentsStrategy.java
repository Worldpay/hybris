package com.worldpay.service.payment;

import com.worldpay.data.FraudSightData;
import com.worldpay.data.GuaranteedPaymentsData;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * Strategy to handle the Guaranteed Payments logic.
 */
public interface WorldpayGuaranteedPaymentsStrategy {

    /**
     * Checks if the Guaranteed Payments is enabled for the session site
     *
     * @return true if enabled, false otherwise
     */
    boolean isGuaranteedPaymentsEnabled();

    /**
     * Checks if the Guaranteed Payments is enabled for the given site
     *
     * @param baseSite the given site
     * @return true if enabled, false otherwise
     */
    boolean isGuaranteedPaymentsEnabled(BaseSiteModel baseSite);

    /**
     * Creates the GuaranteedPaymentsData based on the abstractOrder details and worldpayAdditionalInfoData. Override this method if you want to customize
     * the logic related to the Guaranteed Payments payload
     *
     * @param abstractOrder              the cart
     * @param worldpayAdditionalInfoData the additional info
     * @return a populated {@link FraudSightData}
     */
    GuaranteedPaymentsData createGuaranteedPaymentsData(final AbstractOrderModel abstractOrder, final WorldpayAdditionalInfoData worldpayAdditionalInfoData);
}
