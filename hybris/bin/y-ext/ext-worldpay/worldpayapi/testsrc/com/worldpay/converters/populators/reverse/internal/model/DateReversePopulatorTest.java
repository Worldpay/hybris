package com.worldpay.converters.populators.reverse.internal.model;


import com.worldpay.internal.model.Date;
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
public class DateReversePopulatorTest {

    @InjectMocks
    private DateReversePopulator testObj;

    @Mock
    private Date sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.data.Date());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_ShouldPopulateDate() {
        when(sourceMock.getDayOfMonth()).thenReturn("01");
        when(sourceMock.getMonth()).thenReturn("01");
        when(sourceMock.getYear()).thenReturn("2021");
        when(sourceMock.getHour()).thenReturn("01");
        when(sourceMock.getMinute()).thenReturn("01");
        when(sourceMock.getSecond()).thenReturn("01");

        final com.worldpay.data.Date target = new com.worldpay.data.Date();
        testObj.populate(sourceMock, target);

        assertThat(target.getDayOfMonth()).isEqualTo("01");
        assertThat(target.getHour()).isEqualTo("01");
        assertThat(target.getMinute()).isEqualTo("01");
        assertThat(target.getMonth()).isEqualTo("01");
        assertThat(target.getSecond()).isEqualTo("01");
        assertThat(target.getYear()).isEqualTo("2021");
    }
}
