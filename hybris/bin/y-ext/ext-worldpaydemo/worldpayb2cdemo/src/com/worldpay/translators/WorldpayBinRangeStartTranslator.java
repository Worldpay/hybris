package com.worldpay.translators;

/**
 * This translator is used to define pad char for bin range start values
 */
public class WorldpayBinRangeStartTranslator extends AbstractWorldpayBinRangeTranslator {

    /**
     * Pad start with 0, the lowest digit
     * @return
     */
    @Override
    String getPadChar() {
        return "0";
    }
}
