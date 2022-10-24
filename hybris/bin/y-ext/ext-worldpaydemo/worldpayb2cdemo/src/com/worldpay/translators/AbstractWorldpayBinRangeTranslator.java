package com.worldpay.translators;

import com.worldpay.constants.Worldpayb2cdemoConstants;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang3.StringUtils;

/**
 * This translator is used for bin range start and end transformation, when importing data. It replaces '*' with the
 * defined pad char in the sub classes, and it removes spaces.
 */
public abstract class AbstractWorldpayBinRangeTranslator extends AbstractValueTranslator {

    private ConfigurationService configurationService;

    /**
     * Method responsible for the transformation of bin range value
     *
     * @param valueExpr
     * @param item
     * @return transformed bin range value
     * @throws JaloInvalidParameterException
     */
    public Object importValue(final String valueExpr, final Item item) throws JaloInvalidParameterException {
        final int binRangeCardSize = getConfigurationService().getConfiguration()
                .getInt(Worldpayb2cdemoConstants.BIN_RANGE_CARD_SIZE_PROPERTY, Worldpayb2cdemoConstants.BIN_RANGE_CARD_SIZE_DEFAULT);

        if (valueExpr != null) {
            final String adjustedValueExpr = adjust(valueExpr);
            final String result = adjustedValueExpr.length() < binRangeCardSize
                    ? StringUtils.rightPad(adjustedValueExpr, binRangeCardSize, getPadChar())
                    : adjustedValueExpr.substring(0, binRangeCardSize);
            return Long.valueOf(result);
        }
        return null;
    }

    /**
     * Returns null
     *
     * @param o
     * @return
     * @throws JaloInvalidParameterException
     */
    public String exportValue(final Object o) throws JaloInvalidParameterException {
        return null;
    }

    protected String adjust(final String valueExpr) {
        return valueExpr.replaceAll("\\*", getPadChar()).replace(" ", "");
    }

    protected ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = (ConfigurationService) Registry.getApplicationContext()
                    .getBean("configurationService");
        }
        return configurationService;
    }

    abstract String getPadChar();
}
