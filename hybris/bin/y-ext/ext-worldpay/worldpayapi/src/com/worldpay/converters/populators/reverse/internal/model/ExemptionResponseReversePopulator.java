package com.worldpay.converters.populators.reverse.internal.model;

import com.worldpay.data.Exemption;
import com.worldpay.data.ExemptionResponseInfo;
import com.worldpay.internal.model.ExemptionResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class ExemptionResponseReversePopulator implements Populator<ExemptionResponse, ExemptionResponseInfo> {

    private final Converter<com.worldpay.internal.model.Exemption, Exemption> internalExemptionReverseConverter;

    public ExemptionResponseReversePopulator(final Converter<com.worldpay.internal.model.Exemption, Exemption> internalExemptionReverseConverter) {
        this.internalExemptionReverseConverter = internalExemptionReverseConverter;
    }

    @Override
    public void populate(final ExemptionResponse source, final ExemptionResponseInfo target) throws ConversionException {
        target.setExemption(internalExemptionReverseConverter.convert(source.getExemption()));
        target.setResult(source.getResult());
        target.setReason(source.getReason());
    }
}
