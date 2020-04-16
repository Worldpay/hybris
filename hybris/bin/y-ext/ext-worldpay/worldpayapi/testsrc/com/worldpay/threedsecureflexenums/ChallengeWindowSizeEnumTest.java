package com.worldpay.threedsecureflexenums;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class ChallengeWindowSizeEnumTest {
    private static final String H400 = "400";
    private static final String W250 = "250";
    private static final String ONE_HUNDRED_PERCENT = "100%";
    private ChallengeWindowSizeEnum testObj;

    @Test
    public void testToStringMethodReturnValueWithoutTheInitialCase() {
        testObj = ChallengeWindowSizeEnum.R_250_400;
        assertThat(testObj.toString()).isEqualTo("250x400");
    }

    @Test
    public void testToStringMethodReturnsFullPageWhenTheEnumIsFullPage() {
        testObj = ChallengeWindowSizeEnum.FULL_PAGE;
        assertThat(testObj.toString()).isEqualTo("fullPage");
    }


    @Test
    public void getEnumOfValidResolutionRetrievesTheEnumeration() {
        testObj = ChallengeWindowSizeEnum.getEnum("250x400");
        assertThat(testObj).isEqualTo(ChallengeWindowSizeEnum.R_250_400);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEnumOfInValidResolutionRaisesAnException() {
        testObj = ChallengeWindowSizeEnum.getEnum("250x4002");
    }

    @Test
    public void getEnumOfAnEmptyValueReturnsR390x400WindowChallengeEnum() {
        testObj = ChallengeWindowSizeEnum.getEnum("");
        assertThat(testObj).isEqualTo(ChallengeWindowSizeEnum.R_390_400);
    }

    @Test
    public void getEnumOfANullValueReturnsR390x400WindowChallengeEnum() {
        testObj = ChallengeWindowSizeEnum.getEnum(null);
        assertThat(testObj).isEqualTo(ChallengeWindowSizeEnum.R_390_400);
    }

    @Test
    public void getHeightOfAnExistingResolutionReturnsTheStringValue() {
        testObj = ChallengeWindowSizeEnum.R_250_400;
        final String result = testObj.getHeight();
        assertThat(result).isEqualTo(H400);
    }

    @Test
    public void getWidthOfAnExistingResolutionReturnsTheStringValue() {
        testObj = ChallengeWindowSizeEnum.R_250_400;

        final String result = testObj.getWidth();

        assertThat(result).isEqualTo(W250);
    }

    @Test
    public void getWidthAndHeightFullPageResolutionReturns100PercentForWidthAndHeightTheStringValue() {
        testObj = ChallengeWindowSizeEnum.FULL_PAGE;

        final String resultWidth = testObj.getWidth();
        final String resultHeight = testObj.getHeight();

        assertThat(resultWidth).isEqualTo(ONE_HUNDRED_PERCENT);
        assertThat(resultHeight).isEqualTo(ONE_HUNDRED_PERCENT);
    }
}
