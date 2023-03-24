package com.worldpay.converters.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayDatePopulatorTest {

    @InjectMocks
    private WorldpayDatePopulator testObj;

    private Date sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenSourceIsNull_shouldThrowAnException() {
        testObj.populate(null, new com.worldpay.data.Date());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_whenTargetIsNull_shouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_whenSourceAndTargetAreNotNull_shouldPopulateDate() {
        sourceMock = new Date();

        final LocalDateTime sourceDate = sourceMock.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        final com.worldpay.data.Date target = new com.worldpay.data.Date();
        testObj.populate(sourceMock, target);

        assertThat(target.getYear()).isEqualTo(String.valueOf(sourceDate.getYear()));
        assertThat(target.getMonth()).isEqualTo(String.valueOf(sourceDate.getMonthValue()));
        assertThat(target.getDayOfMonth()).isEqualTo(String.valueOf(sourceDate.getDayOfMonth()));
        assertThat(target.getHour()).isEqualTo(String.valueOf(sourceDate.getHour()));
        assertThat(target.getMinute()).isEqualTo(String.valueOf(sourceDate.getMinute()));
        assertThat(target.getSecond()).isEqualTo(String.valueOf(sourceDate.getSecond()));
    }
}
