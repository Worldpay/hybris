package com.worldpay.converters.populators.internal.model;

import com.worldpay.internal.model.Exclude;
import com.worldpay.internal.model.Include;
import com.worldpay.data.PaymentMethodMask;
import com.worldpay.data.payment.StoredCredentials;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Populator that fills the necessary details on a {@link com.worldpay.internal.model.PaymentMethodMask} with the information of a {@link PaymentMethodMask}.
 */
public class PaymentMethodMaskPopulator implements Populator<PaymentMethodMask, com.worldpay.internal.model.PaymentMethodMask> {

    protected final Converter<StoredCredentials, com.worldpay.internal.model.StoredCredentials> internalStoredCredentialsConverter;

    public PaymentMethodMaskPopulator(final Converter<StoredCredentials, com.worldpay.internal.model.StoredCredentials> internalStoredCredentialsConverter) {
        this.internalStoredCredentialsConverter = internalStoredCredentialsConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final PaymentMethodMask source,
                         final com.worldpay.internal.model.PaymentMethodMask target) throws ConversionException {
        validateParameterNotNull(source, "Source must not be null!");
        validateParameterNotNull(target, "Target list must not be null!");

        final List<Object> includeOrExclude = target.getStoredCredentialsOrIncludeOrExclude();

        Optional.ofNullable(source.getStoredCredentials())
            .map(internalStoredCredentialsConverter::convert)
            .ifPresent(includeOrExclude::add);

        CollectionUtils.emptyIfNull(source.getExcludes()).stream()
            .map(this::createIntExclude)
            .forEach(includeOrExclude::add);

        CollectionUtils.emptyIfNull(source.getIncludes()).stream()
            .map(this::createIntInclude)
            .forEach(includeOrExclude::add);
    }

    /**
     * Creates a {@link com.worldpay.internal.model.Exclude} with the given methodCode.
     *
     * @param methodCode the method code.
     * @return a {@link com.worldpay.internal.model.Exclude}.
     */
    protected Exclude createIntExclude(final String methodCode) {
        final Exclude intExclude = new Exclude();
        intExclude.setCode(methodCode);
        return intExclude;
    }

    /**
     * Creates a {@link com.worldpay.internal.model.Include} with the given methodCode.
     *
     * @param methodCode the method code.
     * @return a {@link com.worldpay.internal.model.Include}.
     */
    protected Include createIntInclude(final String methodCode) {
        final Include intInclude = new Include();
        intInclude.setCode(methodCode);
        return intInclude;
    }
}
