package com.worldpay.service.model.threeds2;

import com.worldpay.internal.helper.InternalModelObject;
import com.worldpay.service.request.transform.InternalModelTransformer;
import com.worldpay.threedsecureflexenums.ChallengePreferenceEnum;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;

import java.io.Serializable;

public class Additional3DSData implements InternalModelTransformer, Serializable {
    private ChallengePreferenceEnum challengePreference;
    private String dfReferenceId;
    private ChallengeWindowSizeEnum challengeWindowSize;

    public Additional3DSData() {
    }

    public Additional3DSData(final String referenceId) {
        this.dfReferenceId = referenceId;
    }

    @Override
    public InternalModelObject transformToInternalModel() {
        final com.worldpay.internal.model.Additional3DSData additional3DSData = new com.worldpay.internal.model.Additional3DSData();
        if (getChallengePreference() != null) {
            additional3DSData.setChallengePreference(getChallengePreference().toString());
        }
        additional3DSData.setDfReferenceId(getDfReferenceId());
        if (getChallengeWindowSize() != null) {
            additional3DSData.setChallengeWindowSize(getChallengeWindowSize().toString());
        }

        return additional3DSData;
    }

    public ChallengePreferenceEnum getChallengePreference() {
        return challengePreference;
    }

    public void setChallengePreference(final ChallengePreferenceEnum challengePreference) {
        this.challengePreference = challengePreference;
    }

    public String getDfReferenceId() {
        return dfReferenceId;
    }

    public void setDfReferenceId(final String dfReferenceId) {
        this.dfReferenceId = dfReferenceId;
    }

    public ChallengeWindowSizeEnum getChallengeWindowSize() {
        return challengeWindowSize;
    }

    public void setChallengeWindowSize(final ChallengeWindowSizeEnum challengeWindowSize) {
        this.challengeWindowSize = challengeWindowSize;
    }
}
