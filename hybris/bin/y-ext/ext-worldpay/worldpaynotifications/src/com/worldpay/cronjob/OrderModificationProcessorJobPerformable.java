package com.worldpay.cronjob;

import com.worldpay.core.services.WorldpayOrderModificationProcessService;
import com.worldpay.worldpaynotifications.model.OrderModificationCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static de.hybris.platform.cronjob.enums.CronJobResult.ERROR;
import static de.hybris.platform.cronjob.enums.CronJobResult.SUCCESS;
import static de.hybris.platform.cronjob.enums.CronJobStatus.FINISHED;

/**
 * The Order Modification Processor Job Performable that processes all pending order modifications for available payment transaction types.
 */
public class OrderModificationProcessorJobPerformable extends AbstractJobPerformable<OrderModificationCronJobModel> {

    protected final WorldpayOrderModificationProcessService worldpayOrderModificationProcessService;

    private static final Logger LOG = LoggerFactory.getLogger(OrderModificationProcessorJobPerformable.class);

    public OrderModificationProcessorJobPerformable(final WorldpayOrderModificationProcessService worldpayOrderModificationProcessService) {
        this.worldpayOrderModificationProcessService = worldpayOrderModificationProcessService;
    }

    @Override
    public PerformResult perform(final OrderModificationCronJobModel cronJobModel) {
        final Set<PaymentTransactionType> typeOfPaymentTransactionToProcessSet = cronJobModel.getPaymentTransactionTypes();
        LOG.info("Executing cronjob for payment transaction types: {}", typeOfPaymentTransactionToProcessSet);
        boolean success = true;
        for (final PaymentTransactionType paymentTransactionType : typeOfPaymentTransactionToProcessSet) {
            if (!worldpayOrderModificationProcessService.processOrderModificationMessages(paymentTransactionType)) {
                success = false;
            }
        }
        final CronJobResult result = success ? SUCCESS : ERROR;
        LOG.info("Cronjob finished with result {}", result);
        return new PerformResult(result, FINISHED);
    }
}
