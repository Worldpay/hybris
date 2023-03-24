package com.worldpay.converters;

import com.worldpay.data.WorldpayBinRangeData;
import com.worldpay.model.WorldpayBinRangeModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayBinRangeConverterTest {

    @InjectMocks
    private WorldpayBinRangeConverter worldpayBinRangeConverter = new WorldpayBinRangeConverter();

    @Test
    public void shouldConvert() throws Exception {
        WorldpayBinRangeModel worldpayBinRangeModel = new WorldpayBinRangeModel();
        worldpayBinRangeModel.setCountryCode("UK");
        worldpayBinRangeModel.setCardIssuer("Issuer");
        worldpayBinRangeModel.setCardType("Visa");

        WorldpayBinRangeData data = worldpayBinRangeConverter.convert(worldpayBinRangeModel);

        Assert.assertEquals("UK", data.getCountryCode());
        Assert.assertEquals("Issuer", data.getCardIssuer());
        Assert.assertEquals("Visa", data.getCardType());
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenSourceIsNull() throws Exception {
        WorldpayBinRangeModel worldpayBinRangeModel = null;

        worldpayBinRangeConverter.convert(worldpayBinRangeModel);
    }
}