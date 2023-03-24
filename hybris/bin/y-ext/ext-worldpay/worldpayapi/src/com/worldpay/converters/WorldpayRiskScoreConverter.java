package com.worldpay.converters;

import com.worldpay.model.WorldpayRiskScoreModel;
import com.worldpay.data.RiskScore;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;

/**
 * Converter to transform a {@link RiskScore} received from Worldpay to a {@link WorldpayRiskScoreModel}
 */
public class WorldpayRiskScoreConverter extends AbstractConverter<RiskScore, WorldpayRiskScoreModel> {

    private ModelService modelService;

    /**
     * Creates a {@link WorldpayRiskScoreModel} with the information from the {@link RiskScore}. Handles RMM and RiskGuardian(tm) RiskScores.
     *
     * @param riskScore the {@link RiskScore} received from Worldpay
     * @return a created {@link WorldpayRiskScoreModel} with the information received.
     */
    @Override
    public WorldpayRiskScoreModel convert(final RiskScore riskScore) {
        final WorldpayRiskScoreModel worldpayRiskScoreModel = modelService.create(WorldpayRiskScoreModel.class);
        populate(riskScore, worldpayRiskScoreModel);
        return worldpayRiskScoreModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final RiskScore riskScore, final WorldpayRiskScoreModel worldpayRiskScoreModel) {
        Optional.ofNullable(riskScore.getFinalScore()).map(Double::valueOf).ifPresent(worldpayRiskScoreModel::setFinalScore);
        worldpayRiskScoreModel.setExtendedResponse(riskScore.getExtendedResponse());
        worldpayRiskScoreModel.setId(riskScore.getId());
        worldpayRiskScoreModel.setMessage(riskScore.getMessage());
        worldpayRiskScoreModel.setProvider(riskScore.getProvider());
        Optional.ofNullable(riskScore.getRGID()).map(Long::valueOf).ifPresent(worldpayRiskScoreModel::setRgid);
        Optional.ofNullable(riskScore.getTRisk()).map(Double::valueOf).ifPresent(worldpayRiskScoreModel::setTRisk);
        Optional.ofNullable(riskScore.getTScore()).map(Double::valueOf).ifPresent(worldpayRiskScoreModel::setTScore);
        worldpayRiskScoreModel.setValue(riskScore.getValue());
        worldpayRiskScoreModel.setTriggeredRules(riskScore.getTriggeredRules());
        Optional.ofNullable(riskScore.getScore()).map(Double::valueOf).ifPresent(worldpayRiskScoreModel::setScore);
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
}
