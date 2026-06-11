package com.worldpay.cronjob;

import com.worldpay.strategies.WorldpayOrderModificationCleanUpStrategy;
import com.worldpay.worldpaynotifications.model.CleanUpProcessedOrderModificationsCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;


/**
 * The Order Modification Clean Up Job Performable that cleans up processed order modifications after a certain amount of time.
 */
public class OrderModificationCleanUpJobPerformable extends AbstractJobPerformable<CleanUpProcessedOrderModificationsCronJobModel> {

    protected final WorldpayOrderModificationCleanUpStrategy worldpayOrderModificationCleanUpStrategy;

    public OrderModificationCleanUpJobPerformable(final WorldpayOrderModificationCleanUpStrategy worldpayOrderModificationCleanUpStrategy) {
        this.worldpayOrderModificationCleanUpStrategy = worldpayOrderModificationCleanUpStrategy;
    }

    @Override
    public PerformResult perform(final CleanUpProcessedOrderModificationsCronJobModel cleanUpOrderModificationsCronJobModel) {
        getWorldpayOrderModificationCleanUpStrategy().doCleanUp(cleanUpOrderModificationsCronJobModel.getDaysToWaitBeforeDeletion());
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public WorldpayOrderModificationCleanUpStrategy getWorldpayOrderModificationCleanUpStrategy() {
        return worldpayOrderModificationCleanUpStrategy;
    }

}
