package com.worldpay.customer.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Extension of {@link DefaultCustomerAccountService} to send a delete token request to Worldpay on a unlink credit card
 */
public class DefaultWorldpayCustomerAccountService extends DefaultCustomerAccountService {

    private UiExperienceService uiExperienceService;

    private WorldpayMerchantInfoService worldpayMerchantInfoService;

    private WorldpayDirectOrderService worldpayDirectOrderService;

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayCustomerAccountService.class);

    /**
     * Fist delete token at Worldpay than call super
     * @param customerModel
     * @param creditCardPaymentInfo
     */
    @Override
    public void unlinkCCPaymentInfo(final CustomerModel customerModel, final CreditCardPaymentInfoModel creditCardPaymentInfo) {
        try {
            final MerchantInfo merchantInfo = getCurrentMerchantInfo();
            worldpayDirectOrderService.deleteToken(merchantInfo, creditCardPaymentInfo);
        } catch (WorldpayException e) {
            LOG.error("Error deleting token at worldpay ", e);
        }
        super.unlinkCCPaymentInfo(customerModel, creditCardPaymentInfo);
    }

    protected MerchantInfo getCurrentMerchantInfo() throws WorldpayConfigurationException {
        final UiExperienceLevel uiExperienceLevel = uiExperienceService.getUiExperienceLevel();
        return worldpayMerchantInfoService.getCurrentSiteMerchant(uiExperienceLevel);
    }

    @Required
    public void setWorldpayDirectOrderService(WorldpayDirectOrderService worldpayDirectOrderService) {
        this.worldpayDirectOrderService = worldpayDirectOrderService;
    }

    @Required
    public void setUiExperienceService(UiExperienceService uiExperienceService) {
        this.uiExperienceService = uiExperienceService;
    }

    @Required
    public void setWorldpayMerchantInfoService(WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

}
