package com.worldpay.service.payment.impl;

import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.payment.WorldpayExemptionStrategy;
import de.hybris.platform.site.BaseSiteService;

import java.util.Set;

public abstract class AbstractWorldpayExemptionStrategy implements WorldpayExemptionStrategy {

    protected static final String EXEMPTION_TYPE_DEFAULT_VALUE = "OP";
    protected static final String EXEMPTION_TYPE_LOW_VALUE = "LV";
    protected static final String EXEMPTION_TYPE_LOW_RISK = "LR";
    protected static final String EXEMPTION_PLACEMENT_DEFAULT_VALUE = "OPTIMISED";
    protected static final String EXEMPTION_PLACEMENT_AUTHORISATION = "AUTHORISATION";
    protected static final String EXEMPTION_PLACEMENT_AUTHENTICATION = "AUTHENTICATION";

    protected static final Set<String> VALID_PAYMENT_TYPES = Set.of(PaymentType.MASTERCARD.getMethodCode(),
                                                                    PaymentType.VISA.getMethodCode(),
                                                                    PaymentType.CARD_SSL.getMethodCode());

    protected final BaseSiteService baseSiteService;

    protected AbstractWorldpayExemptionStrategy(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }
}
