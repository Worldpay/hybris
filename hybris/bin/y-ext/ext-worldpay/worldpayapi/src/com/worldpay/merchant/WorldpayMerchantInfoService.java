package com.worldpay.merchant;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.data.MerchantInfo;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * This interface defines the methods to return the different merchants to be used in the application depending on the current site.
 *
 * @spring.bean WorldpayMerchantInfoService
 */
public interface WorldpayMerchantInfoService {

    /**
     * Returns the merchant configured to be used depending on the UI experience level.
     *
     * @return
     * @throws WorldpayConfigurationException
     */
    MerchantInfo getCurrentSiteMerchant() throws WorldpayConfigurationException;

    /**
     * Returns the merchant configured to be used for order replenishment (B2B)
     *
     * @param site the site for which retrieve the merchant info
     * @return the populated {@link MerchantInfo}
     * @throws WorldpayConfigurationException
     */
    MerchantInfo getReplenishmentMerchant(BaseSiteModel site) throws WorldpayConfigurationException;

    /**
     * Returns the merchantConfiguration for a known merchantCode.
     *
     * @param merchantCode
     * @return
     * @throws WorldpayConfigurationException
     */
    MerchantInfo getMerchantInfoByCode(String merchantCode) throws WorldpayConfigurationException;

    /**
     * Returns the merchantConfiguration for the merchant used in the transaction {@link PaymentTransactionModel}
     *
     * @param paymentTransactionModel
     * @return
     * @throws WorldpayConfigurationException
     */
    MerchantInfo getMerchantInfoFromTransaction(PaymentTransactionModel paymentTransactionModel) throws WorldpayConfigurationException;
}
