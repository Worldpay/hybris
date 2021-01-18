package com.worldpay.cronjob;

import com.worldpay.core.services.WorldpayOrderModificationProcessService;
import com.worldpay.worldpaynotifications.model.OrderModificationCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.Set;

import static de.hybris.platform.cronjob.enums.CronJobResult.ERROR;
import static de.hybris.platform.cronjob.enums.CronJobResult.SUCCESS;
import static de.hybris.platform.cronjob.enums.CronJobStatus.FINISHED;

/**
 * The Order Modification Processor Job Performable that processes all pending order modifications for available payment transaction types.
 */
public class OrderModificationProcessorJobPerformable extends AbstractJobPerformable<OrderModificationCronJobModel> {

    protected final WorldpayOrderModificationProcessService worldpayOrderModificationProcessService;

    private static final Logger LOG = Logger.getLogger(OrderModificationProcessorJobPerformable.class);

    public OrderModificationProcessorJobPerformable(final WorldpayOrderModificationProcessService worldpayOrderModificationProcessService) {
        this.worldpayOrderModificationProcessService = worldpayOrderModificationProcessService;
    }

    @Override
    public PerformResult perform(final OrderModificationCronJobModel cronJobModel) {
        final Set<PaymentTransactionType> typeOfPaymentTransactionToProcessSet = cronJobModel.getPaymentTransactionTypes();
        LOG.info(MessageFormat.format("Executing cronjob for payment transaction types: {0}", typeOfPaymentTransactionToProcessSet));
        boolean success = true;
        for (final PaymentTransactionType paymentTransactionType : typeOfPaymentTransactionToProcessSet) {
            if (!worldpayOrderModificationProcessService.processOrderModificationMessages(paymentTransactionType)) {
                success = false;
            }
        }
        final CronJobResult result = success ? SUCCESS : ERROR;
        LOG.info(MessageFormat.format("Cronjob finished with result {0}", result));
        return new PerformResult(result, FINISHED);
    }
}
