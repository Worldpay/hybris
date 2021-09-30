package com.worldpay.interceptors;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.data.MerchantInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Interceptor that requests the token deletion form Worldpay once a Credit Card (CreditCardPaymentInfoModel) or Alternative Payment Method
 * APM (WorldpayAPMPaymentInfoModel) is deleted from the system.
 */
public class WorldpayPaymentInfoRemoveInterceptor implements RemoveInterceptor<PaymentInfoModel> {

    private static final Logger LOG = LoggerFactory.getLogger(WorldpayPaymentInfoRemoveInterceptor.class);

    protected final WorldpayMerchantInfoService worldpayMerchantInfoService;
    protected final WorldpayDirectOrderService worldpayDirectOrderService;

    public WorldpayPaymentInfoRemoveInterceptor(final WorldpayMerchantInfoService worldpayMerchantInfoService, final WorldpayDirectOrderService worldpayDirectOrderService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
        this.worldpayDirectOrderService = worldpayDirectOrderService;
    }

    @Override
    public void onRemove(final PaymentInfoModel paymentInfoModel, final InterceptorContext interceptorContext) {
        if (paymentInfoModel instanceof CreditCardPaymentInfoModel) {
            final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfoModel;
            if (!shouldNotDeleteToken(creditCardPaymentInfoModel)) {
                deleteToken(creditCardPaymentInfoModel, creditCardPaymentInfoModel.getSubscriptionId());
            }
        } else if (paymentInfoModel instanceof WorldpayAPMPaymentInfoModel) {
            final WorldpayAPMPaymentInfoModel apmPaymentInfoModel = (WorldpayAPMPaymentInfoModel) paymentInfoModel;
            if (!shouldNotDeleteToken(apmPaymentInfoModel)) {
                deleteToken(apmPaymentInfoModel, apmPaymentInfoModel.getSubscriptionId());
            }
        }
    }

    private void deleteToken(final PaymentInfoModel paymentInfoModel, final String subscriptionId) {
        final String merchantId = paymentInfoModel.getMerchantId();
        try {
            final MerchantInfo merchantInfo = worldpayMerchantInfoService.getMerchantInfoByCode(merchantId);
            worldpayDirectOrderService.deleteToken(merchantInfo, paymentInfoModel, subscriptionId);
            LOG.info("Deleting worldpay token for user with authenticated shopper id {}", paymentInfoModel.getAuthenticatedShopperID());
        } catch (final WorldpayConfigurationException e) {
            LOG.error(MessageFormat.format(
                "Could not find merchant configuration for merchant id [{0}]. The token was not deleted in Worldpay - subscription id [{1}]",
                merchantId, subscriptionId), e);
        } catch (final WorldpayException e) {
            LOG.error(MessageFormat.format("Failed to delete token in worldpay, subscription id [{0}]", subscriptionId), e);
        }
    }

    private boolean shouldNotDeleteToken(final CreditCardPaymentInfoModel creditCardPaymentInfoModel) {
        return creditCardPaymentInfoModel.getUser() == null || creditCardPaymentInfoModel.getDuplicate() || creditCardPaymentInfoModel.getSubscriptionId() == null;
    }

    @SuppressWarnings("squid:S1144")
    private boolean shouldNotDeleteToken(final WorldpayAPMPaymentInfoModel apmPaymentInfoModel) {
        return apmPaymentInfoModel.getUser() == null || apmPaymentInfoModel.getDuplicate() || apmPaymentInfoModel.getSubscriptionId() == null;
    }
}
