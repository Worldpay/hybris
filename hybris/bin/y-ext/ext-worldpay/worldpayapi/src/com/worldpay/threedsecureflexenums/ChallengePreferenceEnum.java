package com.worldpay.threedsecureflexenums;

public enum ChallengePreferenceEnum {
    NO_PREFERENCE("noPreference"),

    NO_CHALLENGE_REQUESTED("noChallengeRequested"),

    CHALLENGE_REQUESTED("challengeRequested"),

    CHALLENGE_MANDATED("challangeMandated");

    private final String realValue;

    ChallengePreferenceEnum(final String realValue) {
        this.realValue = realValue;
    }

    public static ChallengePreferenceEnum getEnum(final String enumValue) {
        switch (enumValue) {
            case "noPreference":
                return NO_PREFERENCE;
            case "noChallengeRequested":
                return NO_CHALLENGE_REQUESTED;
            case "challengeRequested":
                return CHALLENGE_REQUESTED;
            case "challangeMandated":
                return CHALLENGE_MANDATED;
            default:
                return null;
        }
    }
}
