package com.worldpay.converters.populators.internal.model.threeds2;

import com.worldpay.data.threeds2.Additional3DSData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.Additional3DSData} with the information of a {@link Additional3DSData}
 */
public class Additional3DSDataPopulator implements Populator<Additional3DSData, com.worldpay.internal.model.Additional3DSData> {

    /**
     * Populates the data from the {@link Additional3DSData} to a {@link com.worldpay.internal.model.Additional3DSData}
     *
     * @param source a {@link Additional3DSData} from Worldpay
     * @param target a {@link com.worldpay.internal.model.Additional3DSData} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final Additional3DSData source, final com.worldpay.internal.model.Additional3DSData target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        Optional.ofNullable(source.getChallengePreference())
            .ifPresent(target::setChallengePreference);

        Optional.ofNullable(source.getChallengeWindowSize())
            .ifPresent(target::setChallengeWindowSize);

        target.setDfReferenceId(source.getDfReferenceId());

    }
}
