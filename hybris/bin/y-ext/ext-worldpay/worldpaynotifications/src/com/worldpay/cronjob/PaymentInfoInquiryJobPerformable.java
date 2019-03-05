package com.worldpay.cronjob;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.core.services.OrderInquiryService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import com.worldpay.strategies.PaymentTransactionRejectionStrategy;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static de.hybris.platform.cronjob.enums.CronJobResult.FAILURE;
import static de.hybris.platform.cronjob.enums.CronJobResult.SUCCESS;
import static de.hybris.platform.cronjob.enums.CronJobStatus.FINISHED;
import static java.text.MessageFormat.format;

/**
 * This job requests missing information for paymentInfos {@link PaymentInfoModel} and handles various timeout scenarios
 * <p>
 * For APM's we don't necessarily get notifications from worldpay, so to be able to reject APMPaymentInfos {@link WorldpayAPMPaymentInfoModel}
 * based on a timeout, we need to inquire if the PaymentInfo is for an APM.
 * Some APMs (such as eNets) are only Orders in Worldpay and do not become Payments. This means that the order inquiry service will not return a
 * Payment Type for that APM. In this situation we will reject all orders with no Payment Type after a configured timeout.
 */
public class PaymentInfoInquiryJobPerformable extends AbstractJobPerformable {

    private static final Logger LOG = Logger.getLogger(PaymentInfoInquiryJobPerformable.class);

    protected static final String WORLDPAY_APM_MINUTES_BEFORE_INQUIRING_TIMEOUT = "worldpay.APM.minutes.before.inquiring.timeout";
    protected static final String WORLDPAY_APM_DAYS_BEFORE_STOP_INQUIRING_TIMEOUT = "worldpay.APM.days.before.stop.inquiring.timeout";

    private static final int DEFAULT_WAIT_IN_MINUTES = 15;
    private static final int DEFAULT_BLANKET_TIME_IN_DAYS = 5;

    private PaymentTransactionRejectionStrategy paymentTransactionRejectionStrategy;
    private OrderInquiryService orderInquiryService;
    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    private ConfigurationService configurationService;
    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDao;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {
        LOG.info("Executing timeout preparation cronjob for pending payment transaction");
        final int waitTime = configurationService.getConfiguration().getInt(WORLDPAY_APM_MINUTES_BEFORE_INQUIRING_TIMEOUT, DEFAULT_WAIT_IN_MINUTES);
        final int blanketTime = configurationService.getConfiguration().getInt(WORLDPAY_APM_DAYS_BEFORE_STOP_INQUIRING_TIMEOUT, DEFAULT_BLANKET_TIME_IN_DAYS);
        final List<PaymentTransactionModel> pendingPaymentTransactions = worldpayPaymentTransactionDao.findPendingPaymentTransactions(waitTime);
        for (final PaymentTransactionModel paymentTransactionModel : pendingPaymentTransactions) {
            if (paymentTransactionIsOverBlanketTime(paymentTransactionModel, blanketTime)) {
                paymentTransactionRejectionStrategy.executeRejection(paymentTransactionModel);
            } else {
                final String requestId = paymentTransactionModel.getRequestId();
                try {
                    final MerchantInfo merchantConfig = worldpayMerchantInfoService.getMerchantInfoFromTransaction(paymentTransactionModel);
                    final OrderInquiryServiceResponse orderInquiryServiceResponse = orderInquiryService.inquirePaymentTransaction(merchantConfig, paymentTransactionModel);
                    LOG.info(format("Processing order Inquiry Service Response for pending payment transaction with worldpay order code [{0}]", requestId));
                    orderInquiryService.processOrderInquiryServiceResponse(paymentTransactionModel, orderInquiryServiceResponse);
                } catch (final WorldpayException e) {
                    LOG.error(format("Error receiving response from Worldpay for orderInquiry with worldpayOrderCode [{0}]. " +
                            "Probably the service is down, or there is a problem with the merchant configuration", requestId), e);
                    return new PerformResult(FAILURE, FINISHED);
                }
            }
        }
        return new PerformResult(SUCCESS, FINISHED);
    }

    private boolean paymentTransactionIsOverBlanketTime(final PaymentTransactionModel paymentTransactionModel, final int blanketTime) {
        return paymentTransactionModel.getCreationtime().before(Date.from(Instant.now().minus(blanketTime, ChronoUnit.DAYS)));
    }

    @Required
    public void setOrderInquiryService(final OrderInquiryService orderInquiryService) {
        this.orderInquiryService = orderInquiryService;
    }

    @Required
    public void setWorldpayPaymentTransactionDao(final WorldpayPaymentTransactionDao worldpayPaymentTransactionDao) {
        this.worldpayPaymentTransactionDao = worldpayPaymentTransactionDao;
    }

    @Required
    public void setWorldpayMerchantInfoService(final WorldpayMerchantInfoService worldpayMerchantInfoService) {
        this.worldpayMerchantInfoService = worldpayMerchantInfoService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Required
    public void setPaymentTransactionRejectionStrategy(final PaymentTransactionRejectionStrategy paymentTransactionRejectionStrategy) {
        this.paymentTransactionRejectionStrategy = paymentTransactionRejectionStrategy;
    }

}
