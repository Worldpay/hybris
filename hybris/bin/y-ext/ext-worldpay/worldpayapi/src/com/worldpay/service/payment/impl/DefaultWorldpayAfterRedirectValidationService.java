package com.worldpay.service.payment.impl;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayMacValidationException;
import com.worldpay.service.mac.MacValidator;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayAfterRedirectValidationService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;

import static com.worldpay.enums.order.AuthorisedStatus.AUTHORISED;

public class DefaultWorldpayAfterRedirectValidationService implements WorldpayAfterRedirectValidationService {
    protected static final Logger LOG = Logger.getLogger(DefaultWorldpayAfterRedirectValidationService.class);
    private static final String KEY_MAC = "mac";
    private static final String KEY_MAC2 = "mac2";
    private static final String ORDER_KEY = "orderKey";
    private static final String PAYMENT_STATUS = "paymentStatus";
    private static final String KEY_PAYMENT_AMOUNT = "paymentAmount";
    private static final String KEY_PAYMENT_CURRENCY = "paymentCurrency";

    protected final MacValidator macValidator;

    public DefaultWorldpayAfterRedirectValidationService(final MacValidator macValidator) {
        this.macValidator = macValidator;
    }

    /**
     * {@inheritDoc}
     */
    public boolean validateRedirectResponse(final MerchantInfo merchantInfo, final Map<String, String> resultMap) {
        final String orderKey = resultMap.get(ORDER_KEY);
        if (StringUtils.isBlank(orderKey)) {
            return false;
        }

        final String paymentStatus = resultMap.get(PAYMENT_STATUS);
        if (shouldValidateMac(merchantInfo, paymentStatus)) {
            final String mac2 = resultMap.getOrDefault(KEY_MAC2, "");
            final String mac = resultMap.getOrDefault(KEY_MAC, mac2);
            final String paymentAmount = resultMap.get(KEY_PAYMENT_AMOUNT);
            final String paymentCurrency = resultMap.get(KEY_PAYMENT_CURRENCY);
            return validateResponse(merchantInfo, orderKey, mac, paymentAmount, paymentCurrency, AuthorisedStatus.valueOf(paymentStatus));
        }
        return true;
    }

    protected boolean shouldValidateMac(final MerchantInfo merchantInfo, final String paymentStatus) {
        return merchantInfo.isUsingMacValidation() && AUTHORISED.name().equalsIgnoreCase(paymentStatus);
    }

    protected boolean validateResponse(final MerchantInfo merchantInfo, final String orderKey, final String mac, final String paymentAmount, final String paymentCurrency, final AuthorisedStatus paymentStatus) {
        try {
            return macValidator.validateResponse(orderKey, mac, paymentAmount, paymentCurrency, paymentStatus, merchantInfo.getMacSecret());
        } catch (final WorldpayMacValidationException e) {
            LOG.error("Mac validation failed - see log for more details", e);
            return false;
        }
    }
}
