package com.worldpay.service.model.threeds2;

import com.worldpay.threedsecureflexenums.ChallengePreferenceEnum;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class Additional3DSDataTest {

    @Test
    public void transformToInternalModelDefaultNoPreferenceAnd390x400() {
        final Additional3DSData testObj = new Additional3DSData();
        testObj.setDfReferenceId("referenceId");

        final com.worldpay.internal.model.Additional3DSData result = (com.worldpay.internal.model.Additional3DSData) testObj.transformToInternalModel();

        assertEquals("noPreference",result.getChallengePreference());
        assertThat(result.getDfReferenceId()).isEqualTo(testObj.getDfReferenceId());
        assertThat(result.getChallengeWindowSize()).isEqualTo(ChallengeWindowSizeEnum.R_390_400.toString());
    }

    @Test
    public void transformToInternalModelWithNoDefaultValues() {
        final Additional3DSData testObj = new Additional3DSData();
        testObj.setDfReferenceId("referenceId");
        testObj.setChallengePreference(ChallengePreferenceEnum.CHALLENGE_MANDATED);
        testObj.setChallengeWindowSize(ChallengeWindowSizeEnum.R_250_400);

        final com.worldpay.internal.model.Additional3DSData result = (com.worldpay.internal.model.Additional3DSData) testObj.transformToInternalModel();

        assertThat(result.getChallengePreference()).isEqualTo(testObj.getChallengePreference().toString());
        assertThat(result.getDfReferenceId()).isEqualTo(testObj.getDfReferenceId());
        assertThat(result.getChallengeWindowSize()).isEqualTo(testObj.getChallengeWindowSize().toString());
    }
}
