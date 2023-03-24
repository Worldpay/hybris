package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.FraudSightData;
import com.worldpay.data.GuaranteedPaymentsData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class RiskEvaluatorConvertersWrapper {

    protected final Converter<FraudSightData, com.worldpay.internal.model.FraudSightData> internalFraudSightDataConverter;
    protected final Converter<GuaranteedPaymentsData, com.worldpay.internal.model.GuaranteedPaymentsData> internalGuaranteedPaymentsDataConverter;

    public RiskEvaluatorConvertersWrapper(final Converter<FraudSightData, com.worldpay.internal.model.FraudSightData> internalFraudSightDataConverter,
                                          final Converter<GuaranteedPaymentsData, com.worldpay.internal.model.GuaranteedPaymentsData> internalGuaranteedPaymentsDataConverter) {

        this.internalFraudSightDataConverter = internalFraudSightDataConverter;
        this.internalGuaranteedPaymentsDataConverter = internalGuaranteedPaymentsDataConverter;
    }
}
