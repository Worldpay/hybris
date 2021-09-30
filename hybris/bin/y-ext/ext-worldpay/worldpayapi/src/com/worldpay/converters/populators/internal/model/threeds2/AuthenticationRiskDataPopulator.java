package com.worldpay.converters.populators.internal.model.threeds2;

import com.worldpay.internal.model.AuthenticationTimestamp;
import com.worldpay.data.Date;
import com.worldpay.data.threeds2.AuthenticationRiskData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.AuthenticationRiskData} with the information of a {@link AuthenticationRiskData}
 */
public class AuthenticationRiskDataPopulator implements Populator<AuthenticationRiskData, com.worldpay.internal.model.AuthenticationRiskData> {

    protected final Converter<Date, com.worldpay.internal.model.Date> internalDateConverter;

    public AuthenticationRiskDataPopulator(final Converter<Date, com.worldpay.internal.model.Date> internalDateConverter) {
        this.internalDateConverter = internalDateConverter;
    }

    /**
     * Populates the data from the {@link AuthenticationRiskData} to a {@link com.worldpay.internal.model.AuthenticationRiskData}
     *
     * @param source a {@link AuthenticationRiskData} from Worldpay
     * @param target a {@link com.worldpay.internal.model.AuthenticationRiskData} in Worldpay.
     * @throws ConversionException
     */
    @Override
    public void populate(final AuthenticationRiskData source, final com.worldpay.internal.model.AuthenticationRiskData target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target must not be null!");

        target.setAuthenticationMethod(source.getAuthenticationMethod());

        Optional.ofNullable(source.getAuthenticationTimestamp())
            .map(this::createIntAuthenticationTimestamp)
            .ifPresent(target::setAuthenticationTimestamp);
    }

    private AuthenticationTimestamp createIntAuthenticationTimestamp(final Date date) {
        final AuthenticationTimestamp intAuthenticationTimestamp = new AuthenticationTimestamp();
        intAuthenticationTimestamp.setDate(internalDateConverter.convert(date));
        return intAuthenticationTimestamp;
    }
}
