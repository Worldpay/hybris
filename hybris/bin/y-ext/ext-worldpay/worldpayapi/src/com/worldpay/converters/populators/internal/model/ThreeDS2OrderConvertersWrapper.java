package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.threeds2.Additional3DSData;
import com.worldpay.data.threeds2.RiskData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class ThreeDS2OrderConvertersWrapper {

    protected final Converter<RiskData, com.worldpay.internal.model.RiskData> internalRiskDataConverter;
    protected final Converter<Additional3DSData, com.worldpay.internal.model.Additional3DSData> internalAdditional3DSDataConverter;

    public ThreeDS2OrderConvertersWrapper(final Converter<RiskData, com.worldpay.internal.model.RiskData> internalRiskDataConverter,
                                          final Converter<Additional3DSData, com.worldpay.internal.model.Additional3DSData> internalAdditional3DSDataConverter) {
        this.internalRiskDataConverter = internalRiskDataConverter;
        this.internalAdditional3DSDataConverter = internalAdditional3DSDataConverter;
    }
}
