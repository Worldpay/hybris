package com.worldpay.commands.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * Class that contains methods and dependencies used by the implemented Commands
 */
public class WorldpayCommand {

    protected final WorldpayMerchantInfoService worldpayMerchantInfoService;
    protected final WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    protected final WorldpayOrderService worldpayOrderService;
    protected final WorldpayServiceGateway worldpayServiceGateway;

    public WorldpayCommand(final WorldpayMerchantInfoService worldpayMerchantInfoService, final WorldpayPaymentTransactionService worldpayPaymentTransactionService, final WorldpayOrderService worldpayOrderService, final WorldpayServiceGateway worldpayServiceGateway) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
        this.worldpayOrderService = worldpayOrderService;
        this.worldpayServiceGateway = worldpayServiceGateway;
    }

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

    public WorldpayMerchantInfoService getWorldpayMerchantInfoService() {
        return worldpayMerchantInfoService;
    }

    public WorldpayPaymentTransactionService getWorldpayPaymentTransactionService() {
        return worldpayPaymentTransactionService;
    }

    public WorldpayOrderService getWorldpayOrderService() {
        return worldpayOrderService;
    }


    public WorldpayServiceGateway getWorldpayServiceGateway() {
        return worldpayServiceGateway;
    }
}
