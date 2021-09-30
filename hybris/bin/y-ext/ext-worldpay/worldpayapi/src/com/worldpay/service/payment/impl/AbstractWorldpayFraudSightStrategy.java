package com.worldpay.service.payment.impl;

import com.worldpay.data.CustomNumericFields;
import com.worldpay.data.CustomStringFields;
import com.worldpay.service.payment.WorldpayFraudSightStrategy;

/**
 * Abstract implementation of {@link WorldpayFraudSightStrategy}
 */
public abstract class AbstractWorldpayFraudSightStrategy implements WorldpayFraudSightStrategy {

    /**
     * Creates and populates the CustomStringFields.
     *
     * @return the {@link CustomStringFields}
     */
    protected abstract CustomStringFields createCustomStringFields();

    /**
     * Creates and populates the CustomNumericFields.
     *
     * @return the {@link CustomNumericFields}
     */
    protected abstract CustomNumericFields createCustomNumericFields();
}
