package com.worldpay.converters.populators.internal.model.threeds2;

import com.worldpay.data.threeds2.Additional3DSData;
import com.worldpay.threedsecureflexenums.ChallengePreferenceEnum;
import com.worldpay.threedsecureflexenums.ChallengeWindowSizeEnum;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class Additional3DSDataPopulatorTest {

    private static final String DF_REFERENCE_ID = "dfReferenceId";
    private static final String CHALLENGE_MANDATED = "challengeMandated";
    private static final String FULL_PAGE = "fullPage";

    @InjectMocks
    private Additional3DSDataPopulator testObj;

    @Mock
    private Additional3DSData sourceMock;


    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Additional3DSData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_ShouldPopulateAdditional3DSData() {
        when(sourceMock.getChallengePreference()).thenReturn(CHALLENGE_MANDATED);
        when(sourceMock.getChallengeWindowSize()).thenReturn(FULL_PAGE);
        when(sourceMock.getDfReferenceId()).thenReturn(DF_REFERENCE_ID);

        final com.worldpay.internal.model.Additional3DSData targetMock = new com.worldpay.internal.model.Additional3DSData();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getChallengePreference()).isEqualTo(ChallengePreferenceEnum.CHALLENGE_MANDATED.toString());
        assertThat(targetMock.getChallengeWindowSize()).isEqualTo(ChallengeWindowSizeEnum.FULL_PAGE.toString());
        assertThat(targetMock.getDfReferenceId()).isEqualTo(DF_REFERENCE_ID);
    }
}
