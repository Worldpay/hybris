package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.CustomNumericFields;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CustomNumericFieldsPopulatorTest {

    @InjectMocks
    private CustomNumericFieldsPopulator testObj;

    @Mock
    private CustomNumericFields sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.CustomNumericFields());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetCustomNumericField1IsNull_ShouldNotPopulateCustomNumericField1() {
        when(sourceMock.getCustomNumericField1()).thenReturn(null);

        final com.worldpay.internal.model.CustomNumericFields target = new com.worldpay.internal.model.CustomNumericFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericField1()).isNull();
    }

    @Test
    public void populate_WhenGetCustomNumericField2IsNull_ShouldNotPopulateCustomNumericField2() {
        when(sourceMock.getCustomNumericField2()).thenReturn(null);

        final com.worldpay.internal.model.CustomNumericFields target = new com.worldpay.internal.model.CustomNumericFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericField2()).isNull();
    }

    @Test
    public void populate_WhenGetCustomNumericField3IsNull_ShouldNotPopulateCustomNumericField3() {
        when(sourceMock.getCustomNumericField3()).thenReturn(null);

        final com.worldpay.internal.model.CustomNumericFields target = new com.worldpay.internal.model.CustomNumericFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericField3()).isNull();
    }

    @Test
    public void populate_WhenGetCustomNumericField4IsNull_ShouldNotPopulateCustomNumericField4() {
        when(sourceMock.getCustomNumericField4()).thenReturn(null);

        final com.worldpay.internal.model.CustomNumericFields target = new com.worldpay.internal.model.CustomNumericFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericField4()).isNull();
    }

    @Test
    public void populate_WhenGetCustomNumericField5IsNull_ShouldNotPopulateCustomNumericField5() {
        when(sourceMock.getCustomNumericField5()).thenReturn(null);

        final com.worldpay.internal.model.CustomNumericFields target = new com.worldpay.internal.model.CustomNumericFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericField5()).isNull();
    }

    @Test
    public void populate_WhenGetCustomNumericField6IsNull_ShouldNotPopulateCustomNumericField6() {
        when(sourceMock.getCustomNumericField6()).thenReturn(null);

        final com.worldpay.internal.model.CustomNumericFields target = new com.worldpay.internal.model.CustomNumericFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericField6()).isNull();
    }

    @Test
    public void populate_WhenGetCustomNumericField7IsNull_ShouldNotPopulateCustomNumericField7() {
        when(sourceMock.getCustomNumericField7()).thenReturn(null);

        final com.worldpay.internal.model.CustomNumericFields target = new com.worldpay.internal.model.CustomNumericFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericField7()).isNull();
    }

    @Test
    public void populate_WhenGetCustomNumericField8IsNull_ShouldNotPopulateCustomNumericField8() {
        when(sourceMock.getCustomNumericField8()).thenReturn(null);

        final com.worldpay.internal.model.CustomNumericFields target = new com.worldpay.internal.model.CustomNumericFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericField8()).isNull();
    }

    @Test
    public void populate_WhenGetCustomNumericField9IsNull_ShouldNotPopulateCustomNumericField9() {
        when(sourceMock.getCustomNumericField9()).thenReturn(null);

        final com.worldpay.internal.model.CustomNumericFields target = new com.worldpay.internal.model.CustomNumericFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericField9()).isNull();
    }

    @Test
    public void populate_WhenGetCustomNumericField10IsNull_ShouldNotPopulateCustomNumericField10() {
        when(sourceMock.getCustomNumericField10()).thenReturn(null);

        final com.worldpay.internal.model.CustomNumericFields target = new com.worldpay.internal.model.CustomNumericFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericField10()).isNull();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getCustomNumericField1()).thenReturn(1);
        when(sourceMock.getCustomNumericField2()).thenReturn(2);
        when(sourceMock.getCustomNumericField3()).thenReturn(3);
        when(sourceMock.getCustomNumericField4()).thenReturn(4);
        when(sourceMock.getCustomNumericField5()).thenReturn(5);
        when(sourceMock.getCustomNumericField6()).thenReturn(6);
        when(sourceMock.getCustomNumericField7()).thenReturn(7);
        when(sourceMock.getCustomNumericField8()).thenReturn(8);
        when(sourceMock.getCustomNumericField9()).thenReturn(9);
        when(sourceMock.getCustomNumericField10()).thenReturn(10);

        final com.worldpay.internal.model.CustomNumericFields target = new com.worldpay.internal.model.CustomNumericFields();
        testObj.populate(sourceMock, target);

        assertThat(target.getCustomNumericField1()).isEqualTo("1");
        assertThat(target.getCustomNumericField2()).isEqualTo("2");
        assertThat(target.getCustomNumericField3()).isEqualTo("3");
        assertThat(target.getCustomNumericField4()).isEqualTo("4");
        assertThat(target.getCustomNumericField5()).isEqualTo("5");
        assertThat(target.getCustomNumericField6()).isEqualTo("6");
        assertThat(target.getCustomNumericField7()).isEqualTo("7");
        assertThat(target.getCustomNumericField8()).isEqualTo("8");
        assertThat(target.getCustomNumericField9()).isEqualTo("9");
        assertThat(target.getCustomNumericField10()).isEqualTo("10");
    }
}
