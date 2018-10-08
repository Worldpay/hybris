package com.worldpay.customer.impl;

import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Extension of {@link DefaultCustomerAccountService} to send a delete token request to Worldpay on a unlink credit card
 */
public class DefaultWorldpayCustomerAccountService extends DefaultCustomerAccountService {

    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    private WorldpayDirectOrderService worldpayDirectOrderService;

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayCustomerAccountService.class);

    /**
     * Fist delete token at Worldpay than call super
     *
     * @param customerModel
     * @param creditCardPaymentInfo
     */
    @Override
    public void deleteCCPaymentInfo(final CustomerModel customerModel, final CreditCardPaymentInfoModel creditCardPaymentInfo) {
        try {
            final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
            worldpayDirectOrderService.deleteToken(merchantInfo, creditCardPaymentInfo);
        } catch (final WorldpayException e) {
            LOG.error("Error deleting token at worldpay ", e);
        }
        super.deleteCCPaymentInfo(customerModel, creditCardPaymentInfo);
    }

    @Required
    public void setWorldpayDirectOrderService(final WorldpayDirectOrderService worldpayDirectOrderService) {
        this.worldpayDirectOrderService = worldpayDirectOrderService;
    }

    @Required
    public void setWorldpayMerchantInfoService(final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

}
