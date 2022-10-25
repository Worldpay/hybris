package com.worldpay.translators;

/**
 * This translator is used to define pad char for bin range end values
 */
public class WorldpayBinRangeEndTranslator extends AbstractWorldpayBinRangeTranslator {

    /**
     * Pad end with 9, the highest digit
     * @return
     */
    @Override
    protected String getPadChar() {
        return "9";
    }
}
