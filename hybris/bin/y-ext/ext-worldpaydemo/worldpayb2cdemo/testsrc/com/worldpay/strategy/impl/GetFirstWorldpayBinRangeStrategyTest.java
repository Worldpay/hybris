package com.worldpay.strategy.impl;

import com.worldpay.model.WorldpayBinRangeModel;
import com.worldpay.strategy.WorldpayBinRangeStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GetFirstWorldpayBinRangeStrategyTest {

    private WorldpayBinRangeStrategy worldpayBinRangeStrategy = new GetFirstWorldpayBinRangeStrategy();
    private WorldpayBinRangeModel binRangeOne = new WorldpayBinRangeModel();
    private WorldpayBinRangeModel binRangeTwo = new WorldpayBinRangeModel();

    @Test
    public void testShouldReturnFirstFromListOfBinRanges() throws Exception {
        Assert.assertEquals(binRangeOne, worldpayBinRangeStrategy.selectBinRange(new ArrayList<>(Arrays.asList(binRangeOne, binRangeTwo))));
    }

    @Test
    public void getShouldReturnNullForEmptyList() throws Exception {
        Assert.assertNull(worldpayBinRangeStrategy.selectBinRange(Collections.EMPTY_LIST));
    }

}