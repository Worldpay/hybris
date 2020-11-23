package com.worldpay.service.model;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DateTest {

    private Date testObj;

    @Test
    public void transformToInternalModel_ShouldReturnInternalDate_WhenUsingFullMembersConstructor() {
        testObj = new Date("15", "5", "2020", "12", "30", "35");

        final com.worldpay.internal.model.Date result = testObj.transformToInternalModel();

        assertThat(result.getDayOfMonth()).isEqualTo("15");
        assertThat(result.getMonth()).isEqualTo("5");
        assertThat(result.getYear()).isEqualTo("2020");
        assertThat(result.getHour()).isEqualTo("12");
        assertThat(result.getMinute()).isEqualTo("30");
        assertThat(result.getSecond()).isEqualTo("35");
    }

    @Test
    public void transformToInternalModel_ShouldReturnInternalDate_WhenLocalDateTimeConstructor() {
        testObj = new Date(LocalDateTime.of(2020, 5, 15, 12, 30, 35));

        final com.worldpay.internal.model.Date result = testObj.transformToInternalModel();

        assertThat(result.getDayOfMonth()).isEqualTo("15");
        assertThat(result.getMonth()).isEqualTo("5");
        assertThat(result.getYear()).isEqualTo("2020");
        assertThat(result.getHour()).isEqualTo("12");
        assertThat(result.getMinute()).isEqualTo("30");
        assertThat(result.getSecond()).isEqualTo("35");
    }

    @Test
    public void transformToInternalModel_ShouldReturnInternalDate_WhenUsingMonthAndYearConstructor() {
        testObj = new Date("5", "2020");

        final com.worldpay.internal.model.Date result = testObj.transformToInternalModel();

        assertThat(result.getDayOfMonth()).isNullOrEmpty();
        assertThat(result.getMonth()).isEqualTo("5");
        assertThat(result.getYear()).isEqualTo("2020");
        assertThat(result.getHour()).isNullOrEmpty();
        assertThat(result.getMinute()).isNullOrEmpty();
        assertThat(result.getSecond()).isNullOrEmpty();
    }
}
