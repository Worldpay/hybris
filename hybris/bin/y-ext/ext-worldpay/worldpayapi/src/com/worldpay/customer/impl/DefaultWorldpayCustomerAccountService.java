package com.worldpay.customer.impl;

import com.worldpay.customer.WorldpayCustomerAccountService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Extension of {@link DefaultCustomerAccountService} to send delete token requests to Worldpay
 * Implements {@link WorldpayCustomerAccountService} to Handle APM payment info deletion
 */
public class DefaultWorldpayCustomerAccountService extends DefaultCustomerAccountService implements WorldpayCustomerAccountService {

    protected WorldpayMerchantInfoService worldpayMerchantInfoService;
    protected WorldpayDirectOrderService worldpayDirectOrderService;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorldpayCustomerAccountService.class);

    /**
     * Fist delete token at Worldpay than call super
     *
     * @param customerModel
     * @param creditCardPaymentInfo
     */
    @Override
    public void deleteCCPaymentInfo(final CustomerModel customerModel, final CreditCardPaymentInfoModel creditCardPaymentInfo) {
        validateParameterNotNull(customerModel, "Customer model cannot be null");
        validateParameterNotNull(creditCardPaymentInfo, "CreditCardPaymentInfo model cannot be null");
        if (customerModel.getPaymentInfos().contains(creditCardPaymentInfo)) {
            deleteWorldpayPaymentInfoToken(creditCardPaymentInfo, creditCardPaymentInfo.getSubscriptionId());
            super.deleteCCPaymentInfo(customerModel, creditCardPaymentInfo);
        } else {
            throw new IllegalArgumentException("Payment Info " + creditCardPaymentInfo
                + " does not belong to the customer " + customerModel + " and will not be removed.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAPMPaymentInfo(final CustomerModel customerModel, final WorldpayAPMPaymentInfoModel paymentInfoModel) {
        validateParameterNotNull(customerModel, "Customer model cannot be null");
        validateParameterNotNull(paymentInfoModel, "CreditCardPaymentInfo model cannot be null");
        if (customerModel.getPaymentInfos().contains(paymentInfoModel)) {
            deleteWorldpayPaymentInfoToken(paymentInfoModel, paymentInfoModel.getSubscriptionId());
            paymentInfoModel.setSaved(false);
            getModelService().save(paymentInfoModel);
            getModelService().refresh(customerModel);
        } else {
            throw new IllegalArgumentException("Payment Info " + paymentInfoModel
                + " does not belong to the customer " + customerModel + " and will not be removed.");
        }
    }

    private void deleteWorldpayPaymentInfoToken(final PaymentInfoModel paymentInfoModel, final String subscriptionId) {
        try {
            final MerchantInfo merchantInfo = worldpayMerchantInfoService.getCurrentSiteMerchant();
            worldpayDirectOrderService.deleteToken(merchantInfo, paymentInfoModel, subscriptionId);
        } catch (final WorldpayException e) {
            LOG.error("Error deleting token at worldpay ", e);
        }
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
