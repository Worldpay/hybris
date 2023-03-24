package com.worldpay.dao.impl;

import com.worldpay.dao.WorldpayBinRangeDao;
import com.worldpay.model.WorldpayBinRangeModel;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class DefaultWorldpayBinRangeDaoTest extends ServicelayerTransactionalTest {

    @Resource
    private ModelService modelService;

    @Resource
    private WorldpayBinRangeDao worldpayBinRangeDao;

    @Before
    public void setUp() throws Exception {
        createBinRange(123456111111L, 123456222222L);
        createBinRange(123456333333L, 123456333333L);
        createBinRange(123457111111L, 123458111111L);
    }

    @Test
    public void shouldReturnZeroBinRanges() throws Exception {
        List<WorldpayBinRangeModel> result = worldpayBinRangeDao.findBinRanges("123455");
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnTwoBinRanges() throws Exception {
        List<WorldpayBinRangeModel> result = worldpayBinRangeDao.findBinRanges("123456");
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(1, result.stream()
                .filter(range -> range.getCardBinRangeStart() == 123456111111L && range.getCardBinRangeEnd() == 123456222222L)
                .count());
        Assert.assertEquals(1, result.stream()
                .filter(range -> range.getCardBinRangeStart() == 123456333333L && range.getCardBinRangeEnd() == 123456333333L)
                .count());
    }

    @Test
    public void shouldReturnOneBinRanges() throws Exception {
        List<WorldpayBinRangeModel> result = worldpayBinRangeDao.findBinRanges("123457");
        Assert.assertEquals(1, result.size());
        WorldpayBinRangeModel model = result.get(0);
        assertThat(123457111111L).isEqualTo(model.getCardBinRangeStart());
        assertThat(123458111111L).isEqualTo(model.getCardBinRangeEnd());
    }

    private void createBinRange(final long start, final long end) {
        WorldpayBinRangeModel model = new WorldpayBinRangeModel();
        model.setCardBinRangeStartOriginal(Long.toString(start));
        model.setCardBinRangeEndOriginal(Long.toString(end));
        model.setCardBinRangeStart(start);
        model.setCardBinRangeEnd(end);
        modelService.save(model);
    }
}
