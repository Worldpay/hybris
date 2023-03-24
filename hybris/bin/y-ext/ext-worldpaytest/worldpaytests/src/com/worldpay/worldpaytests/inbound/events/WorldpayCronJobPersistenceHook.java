package com.worldpay.worldpaytests.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.servicelayer.cronjob.CronJobService;

import java.util.Optional;

public class WorldpayCronJobPersistenceHook implements PrePersistHook {

    private final CronJobService cronJobService;

    public WorldpayCronJobPersistenceHook(final CronJobService cronJobService) {
        this.cronJobService = cronJobService;
    }

    @Override
    public Optional<ItemModel> execute(final ItemModel item) {
        if (item instanceof CronJobModel) {
            final CronJobModel foundCronJob = cronJobService.getCronJob(((CronJobModel) item).getCode());
            cronJobService.performCronJob(foundCronJob);
        }
        return Optional.empty();
    }
}
