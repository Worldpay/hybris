package com.worldpay.cronjob;

import com.worldpay.strategies.WorldpayOrderModificationNotifierStrategy;
import com.worldpay.worldpaynotifications.model.NotifyUnprocessedOrderModificationsCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;


/**
 * The Order Modification Notifier Job Performable that notifies about each unprocessed, stale order modification
 */
public class OrderModificationNotifierJobPerformable extends AbstractJobPerformable<NotifyUnprocessedOrderModificationsCronJobModel> {

    protected final WorldpayOrderModificationNotifierStrategy worldpayOrderModificationNotifierStrategy;

    public OrderModificationNotifierJobPerformable(final WorldpayOrderModificationNotifierStrategy worldpayOrderModificationNotifierStrategy) {
        this.worldpayOrderModificationNotifierStrategy = worldpayOrderModificationNotifierStrategy;
    }

    @Override
    public PerformResult perform(final NotifyUnprocessedOrderModificationsCronJobModel cronJobModel) {
        getWorldpayOrderModificationNotifierStrategy().notifyThatOrdersHaveNotBeenProcessed(cronJobModel.getUnprocessedTimeInDays());
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public WorldpayOrderModificationNotifierStrategy getWorldpayOrderModificationNotifierStrategy() {
        return worldpayOrderModificationNotifierStrategy;
    }

}
