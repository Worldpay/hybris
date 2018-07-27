package com.worldpay.commands.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.springframework.beans.factory.annotation.Required;

/**
 * Class that contains methods and dependencies used by the implemented Commands
 */
public class WorldpayCommand {

    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    private WorldpayOrderService worldpayOrderService;
    private WorldpayServiceGateway worldpayServiceGateway;

    /**
     * Returns the MerchantInfo {@link MerchantInfo} used in the transaction
     *
     * @param worldpayOrderCode order code used in the transaction
     * @return The MerchantInfo used in the transaction
     * @throws WorldpayConfigurationException
     */
    protected MerchantInfo getMerchantInfo(final String worldpayOrderCode) throws WorldpayConfigurationException {
        final PaymentTransactionModel paymentTransactionModel = worldpayPaymentTransactionService.getPaymentTransactionFromCode(worldpayOrderCode);
        return worldpayMerchantInfoService.getMerchantInfoFromTransaction(paymentTransactionModel);
    }

    @Required
    public void setWorldpayMerchantInfoService(final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

    public WorldpayMerchantInfoService getWorldpayMerchantInfoService() {
        return worldpayMerchantInfoService;
    }

    public WorldpayPaymentTransactionService getWorldpayPaymentTransactionService() {
        return worldpayPaymentTransactionService;
    }

    @Required
    public void setWorldpayPaymentTransactionService(final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    @Required
    public void setWorldpayServiceGateway(final WorldpayServiceGateway worldpayServiceGateway) {
        this.worldpayServiceGateway = worldpayServiceGateway;
    }

    public WorldpayOrderService getWorldpayOrderService() {
        return worldpayOrderService;
    }

    @Required
    public void setWorldpayOrderService(final WorldpayOrderService worldpayOrderService) {
        this.worldpayOrderService = worldpayOrderService;
    }

    public WorldpayServiceGateway getWorldpayServiceGateway() {
        return worldpayServiceGateway;
    }
}
