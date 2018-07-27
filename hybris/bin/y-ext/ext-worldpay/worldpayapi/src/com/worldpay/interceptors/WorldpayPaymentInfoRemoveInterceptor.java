package com.worldpay.interceptors;

import com.worldpay.core.services.impl.DefaultWorldpayPaymentInfoService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;

/**
 * Interceptor that requests the token deletion form Worldpay once a Credit Card (CreditCardPaymentInfoModel) is deleted
 * from the system.
 */
public class WorldpayPaymentInfoRemoveInterceptor implements RemoveInterceptor<PaymentInfoModel> {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayPaymentInfoService.class);

    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    private WorldpayDirectOrderService worldpayDirectOrderService;

    @Override
    public void onRemove(final PaymentInfoModel paymentInfoModel, final InterceptorContext interceptorContext) {
        if (!(paymentInfoModel instanceof CreditCardPaymentInfoModel)) {
            return;
        }
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfoModel;
        if (shouldNotDeleteToken(creditCardPaymentInfoModel)) {
            return;
        }
        final String merchantId = creditCardPaymentInfoModel.getMerchantId();
        try {
            final MerchantInfo merchantInfo = worldpayMerchantInfoService.getMerchantInfoByCode(merchantId);
            worldpayDirectOrderService.deleteToken(merchantInfo, creditCardPaymentInfoModel);
            LOG.info(MessageFormat.format("Deleting worldpay token for user with authenticated shopper id {0}",
                    creditCardPaymentInfoModel.getAuthenticatedShopperID()));
        } catch (final WorldpayConfigurationException e) {
            LOG.error(MessageFormat.format(
                    "Could not find merchant configuration for merchant id [{0}]. The token was not deleted in Worldpay - subscription id [{1}]",
                    merchantId, creditCardPaymentInfoModel.getSubscriptionId()), e);
        } catch (final WorldpayException e) {
            LOG.error(MessageFormat.format("Failed to delete token in worldpay, subscription id [{0}]", creditCardPaymentInfoModel.getSubscriptionId()), e);
        }
    }

    private boolean shouldNotDeleteToken(final CreditCardPaymentInfoModel creditCardPaymentInfoModel) {
        return creditCardPaymentInfoModel.getUser() == null || creditCardPaymentInfoModel.getDuplicate() || creditCardPaymentInfoModel.getSubscriptionId() == null;
    }

    @Required
    public void setWorldpayMerchantInfoService(final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

    @Required
    public void setWorldpayDirectOrderService(final WorldpayDirectOrderService worldpayDirectOrderService) {
        this.worldpayDirectOrderService = worldpayDirectOrderService;
    }
}
