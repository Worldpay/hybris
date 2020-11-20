package com.worldpay.threedsecureflexenums;

import org.apache.commons.lang.StringUtils;

public enum ChallengeWindowSizeEnum {
    R_250_400("250x400"),
    R_390_400("390x400"),
    R_500_600("500x600"),
    R_600_400("600x400"),
    FULL_PAGE("fullPage");

    private static final String R_PREFIX = "R";
    private static final String ONE_HUNDRED_PER_CENT = "100%";

    protected final String realValue;

    ChallengeWindowSizeEnum(final String realValue) {
        this.realValue = realValue;
    }

    @Override
    public String toString() {
        return this.realValue;
    }

    /**
     * If no res is returned the default value (R390x400)
     * @param res
     * @return the enum
     */
    public static ChallengeWindowSizeEnum getEnum(final String res) {
        if(StringUtils.isNotBlank(res)) {
            final String replacedRes = StringUtils.replace(res, "x", "_");
            if (Character.isDigit(replacedRes.charAt(0))) {
                return ChallengeWindowSizeEnum.valueOf(R_PREFIX + "_" + replacedRes);
            } else if (res.equals(ChallengeWindowSizeEnum.FULL_PAGE.toString())) {
                return FULL_PAGE;
            } else {
                throw new IllegalArgumentException("Invalid resolution specified");
            }
        } else {
            return R_390_400;
        }
    }

    public String getHeight() {
        if (!equals(FULL_PAGE)) {
            return StringUtils.substringAfter(toString(), "x");
        }

        return ONE_HUNDRED_PER_CENT;
    }

    public String getWidth() {
        if (!equals(FULL_PAGE)) {
            return StringUtils.substringBefore(toString(), "x");
        }

        return ONE_HUNDRED_PER_CENT;
    }
}
