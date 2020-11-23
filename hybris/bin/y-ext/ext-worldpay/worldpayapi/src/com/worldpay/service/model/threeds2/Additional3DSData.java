package com.worldpay.service.model.threeds2;

import com.worldpay.service.request.transform.InternalModelTransformer;
import com.worldpay.threedsecureflexenums.ChallengePreferenceEnum;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;

import java.io.Serializable;

public class Additional3DSData implements InternalModelTransformer, Serializable {
    private String dfReferenceId;
    private ChallengePreferenceEnum challengePreference;
    private ChallengeWindowSizeEnum challengeWindowSize;

    public Additional3DSData() {
    }

    public Additional3DSData(final String referenceId) {
        this.dfReferenceId = referenceId;
    }

    @Override
    public com.worldpay.internal.model.Additional3DSData transformToInternalModel() {
        final com.worldpay.internal.model.Additional3DSData intAdditional3DSData = new com.worldpay.internal.model.Additional3DSData();
        intAdditional3DSData.setDfReferenceId(getDfReferenceId());
        if (challengePreference != null) {
            intAdditional3DSData.setChallengePreference(challengePreference.toString());
        }
        if (challengeWindowSize != null) {
            intAdditional3DSData.setChallengeWindowSize(challengeWindowSize.toString());
        }

        return intAdditional3DSData;
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
