package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.Date;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DatePopulatorTest {

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY_OF_MONTH = "dayOfMonth";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String SECOND = "second";

    @InjectMocks
    private DatePopulator testObj;

    @Mock
    private Date sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.Date());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNull_ShouldPopulate() {
        when(sourceMock.getYear()).thenReturn(YEAR);
        when(sourceMock.getMonth()).thenReturn(MONTH);
        when(sourceMock.getDayOfMonth()).thenReturn(DAY_OF_MONTH);
        when(sourceMock.getHour()).thenReturn(HOUR);
        when(sourceMock.getMinute()).thenReturn(MINUTE);
        when(sourceMock.getSecond()).thenReturn(SECOND);

        final com.worldpay.internal.model.Date target = new com.worldpay.internal.model.Date();
        testObj.populate(sourceMock, target);

        assertThat(target.getYear()).isEqualTo(YEAR);
        assertThat(target.getMonth()).isEqualTo(MONTH);
        assertThat(target.getDayOfMonth()).isEqualTo(DAY_OF_MONTH);
        assertThat(target.getHour()).isEqualTo(HOUR);
        assertThat(target.getMinute()).isEqualTo(MINUTE);
        assertThat(target.getSecond()).isEqualTo(SECOND);
    }
}
