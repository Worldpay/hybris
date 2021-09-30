package com.worldpay.converters.populators;

import com.worldpay.data.apm.WorldpayAPMConfigurationData;
import com.worldpay.data.cms.WorldpayAPMComponentData;
import com.worldpay.model.WorldpayAPMComponentModel;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populates the {@link WorldpayAPMComponentData} from {@link WorldpayAPMComponentModel}
 */
public class WorldpayAPMComponentPopulator implements Populator<WorldpayAPMComponentModel, WorldpayAPMComponentData> {

    protected final Converter<WorldpayAPMConfigurationModel, WorldpayAPMConfigurationData> worldpayAPMConfigurationConverter;
    protected final Converter<MediaModel, MediaData> mediaModelConverter;

    public WorldpayAPMComponentPopulator(final Converter<WorldpayAPMConfigurationModel, WorldpayAPMConfigurationData> worldpayAPMConfigurationConverter,
                                         final Converter<MediaModel, MediaData> mediaModelConverter) {
        this.worldpayAPMConfigurationConverter = worldpayAPMConfigurationConverter;
        this.mediaModelConverter = mediaModelConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final WorldpayAPMComponentModel source,
                         final WorldpayAPMComponentData target) throws ConversionException {
        validateParameterNotNull(source, "source must not be null!");
        validateParameterNotNull(target, "target must not be null!");

        target.setApmConfiguration(worldpayAPMConfigurationConverter.convert(source.getApmConfiguration()));
        Optional.ofNullable(source.getMedia()).ifPresent(mediaModel -> target.setMedia(mediaModelConverter.convert(mediaModel)));
    }
}
