package com.worldpay.service.impl;

import com.worldpay.dao.WorldpayBinRangeDao;
import com.worldpay.model.WorldpayBinRangeModel;
import com.worldpay.service.WorldpayBinRangeService;
import com.worldpay.strategy.WorldpayBinRangeStrategy;
import com.worldpay.strategy.impl.GetFirstWorldpayBinRangeStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayBinRangeServiceTest {

    @InjectMocks
    private DefaultWorldpayBinRangeService defaultWorldpayBinRangeService;
    @Mock
    private WorldpayBinRangeDao worldpayBinRangeDao;
    @Mock
    private WorldpayBinRangeModel binRangeOne;
    @Mock
    private WorldpayBinRangeModel binRangeTwo;

    final private static String CARD_PREFIX = "123456";
    private WorldpayBinRangeStrategy worldpayBinRangeStrategy = new GetFirstWorldpayBinRangeStrategy();

    @Before
    public void setUp() {
        defaultWorldpayBinRangeService.setWorldpayBinRangeStrategy(worldpayBinRangeStrategy);
    }

    @Test
    public void testShouldReturnFirstFromListOfBinRanges() throws Exception {
        Mockito.when(worldpayBinRangeDao.findBinRanges(CARD_PREFIX)).thenReturn(new ArrayList<>(Arrays.asList(binRangeOne, binRangeTwo)));

        WorldpayBinRangeModel result = defaultWorldpayBinRangeService.getBinRange(CARD_PREFIX);

        Assert.assertEquals(binRangeOne, result);
    }

    @Test
    public void getShouldReturnNullForEmptyList() throws Exception {
        Mockito.when(worldpayBinRangeDao.findBinRanges(CARD_PREFIX)).thenReturn(Collections.EMPTY_LIST);

        WorldpayBinRangeModel result = defaultWorldpayBinRangeService.getBinRange(CARD_PREFIX);

        Assert.assertNull(result);
    }

}
