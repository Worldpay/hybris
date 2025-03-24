package com.worldpay.worldpayresponsemock.builders;

import com.worldpay.internal.model.Exemption;
import com.worldpay.internal.model.ExemptionResponse;

public final class ExemptionResponseBuilder {

    private String exemptionResponseReason;
    private String exemptionResponseResult;
    private String exemptionType;
    private String exemptionPlacement;

    private ExemptionResponseBuilder() {
    }

    public static ExemptionResponseBuilder aExemptionResponseBuilder() { return new ExemptionResponseBuilder(); }

    public ExemptionResponseBuilder withExemptionResponseReason(final String exemptionResponseReason) {
        this.exemptionResponseReason = exemptionResponseReason;
        return this;
    }

    public ExemptionResponseBuilder withExemptionResponseResult(final String exemptionResponseResult) {
        this.exemptionResponseResult = exemptionResponseResult;
        return this;
    }

    public ExemptionResponseBuilder withExemptionType(final String exemptionType) {
        this.exemptionType = exemptionType;
        return this;
    }

    public ExemptionResponseBuilder withExemptionPlacement(final String exemptionPlacement) {
        this.exemptionPlacement = exemptionPlacement;
        return this;
    }

    public ExemptionResponse build() {
        final Exemption exemption = new Exemption();
        exemption.setType(exemptionType);
        exemption.setPlacement(exemptionPlacement);

        final ExemptionResponse exemptionResponse = new ExemptionResponse();
        exemptionResponse.setReason(exemptionResponseReason);
        exemptionResponse.setResult(exemptionResponseResult);
        exemptionResponse.setExemption(exemption);

        return exemptionResponse;
    }
}
