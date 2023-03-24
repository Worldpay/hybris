package com.worldpay.translators;

import com.worldpay.constants.Worldpayb2cdemoConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayBinRangeEndTranslatorTest {

    @InjectMocks
    private WorldpayBinRangeEndTranslator worldpayBinRangeEndTranslator = new WorldpayBinRangeEndTranslator();

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Before
    public void setUp() {
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getInt(Worldpayb2cdemoConstants.BIN_RANGE_CARD_SIZE_PROPERTY, Worldpayb2cdemoConstants.BIN_RANGE_CARD_SIZE_DEFAULT))
                .thenReturn(Worldpayb2cdemoConstants.BIN_RANGE_CARD_SIZE_DEFAULT);
    }

    @Test
    public void testAdjustingInBinRangeEnd() {
        Assert.assertEquals(Long.valueOf(123456789999L), worldpayBinRangeEndTranslator.importValue("12 34 56789***", null));
        Assert.assertEquals(Long.valueOf(123456789999L), worldpayBinRangeEndTranslator.importValue("12 34 56789", null));
        Assert.assertEquals(Long.valueOf(123456789012L), worldpayBinRangeEndTranslator.importValue("12 34 567890123", null));
        Assert.assertEquals(Long.valueOf(123456789999L), worldpayBinRangeEndTranslator.importValue("123456789***", null));
        Assert.assertEquals(Long.valueOf(123456789999L), worldpayBinRangeEndTranslator.importValue("123456789", null));
        Assert.assertEquals(Long.valueOf(123456789012L), worldpayBinRangeEndTranslator.importValue("1234567890123", null));
        Assert.assertEquals(Long.valueOf(123456789012L), worldpayBinRangeEndTranslator.importValue("1234567890123 ", null));
        Assert.assertEquals(Long.valueOf(123456789012L), worldpayBinRangeEndTranslator.importValue(" 1234567890123", null));
        Assert.assertEquals(Long.valueOf(123456789012L), worldpayBinRangeEndTranslator.importValue(" 1234567890123 ", null));
        Assert.assertEquals(Long.valueOf(123456789012L), worldpayBinRangeEndTranslator.importValue(" 1234567890123*", null));
    }

}
