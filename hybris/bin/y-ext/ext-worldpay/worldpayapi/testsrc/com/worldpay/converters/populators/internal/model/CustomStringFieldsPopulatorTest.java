package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.CustomStringFields;
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
public class CustomStringFieldsPopulatorTest {

    private static final String CUSTOMER_STRING_FIELD_1 = "customerStringField1";
    private static final String CUSTOMER_STRING_FIELD_2 = "customerStringField2";
    private static final String CUSTOMER_STRING_FIELD_3 = "customerStringField3";
    private static final String CUSTOMER_STRING_FIELD_4 = "customerStringField4";
    private static final String CUSTOMER_STRING_FIELD_5 = "customerStringField5";
    private static final String CUSTOMER_STRING_FIELD_6 = "customerStringField6";
    private static final String CUSTOMER_STRING_FIELD_7 = "customerStringField7";
    private static final String CUSTOMER_STRING_FIELD_8 = "customerStringField8";
    private static final String CUSTOMER_STRING_FIELD_9 = "customerStringField9";
    private static final String CUSTOMER_STRING_FIELD_10 = "customerStringField10";

    @InjectMocks
    private CustomStringFieldsPopulator testObj;

    @Mock
    private CustomStringFields sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhensourceMockIsNull_ShouldThrowException() {
        testObj.populate(null, new com.worldpay.internal.model.CustomStringFields());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhentargetMockIsNull_ShouldThrowException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_ShouldPopulateBrowser() {
        when(sourceMock.getCustomStringField1()).thenReturn(CUSTOMER_STRING_FIELD_1);
        when(sourceMock.getCustomStringField2()).thenReturn(CUSTOMER_STRING_FIELD_2);
        when(sourceMock.getCustomStringField3()).thenReturn(CUSTOMER_STRING_FIELD_3);
        when(sourceMock.getCustomStringField4()).thenReturn(CUSTOMER_STRING_FIELD_4);
        when(sourceMock.getCustomStringField5()).thenReturn(CUSTOMER_STRING_FIELD_5);
        when(sourceMock.getCustomStringField6()).thenReturn(CUSTOMER_STRING_FIELD_6);
        when(sourceMock.getCustomStringField7()).thenReturn(CUSTOMER_STRING_FIELD_7);
        when(sourceMock.getCustomStringField8()).thenReturn(CUSTOMER_STRING_FIELD_8);
        when(sourceMock.getCustomStringField9()).thenReturn(CUSTOMER_STRING_FIELD_9);
        when(sourceMock.getCustomStringField10()).thenReturn(CUSTOMER_STRING_FIELD_10);

        final com.worldpay.internal.model.CustomStringFields targetMock = new com.worldpay.internal.model.CustomStringFields();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getCustomStringField1()).isEqualTo(CUSTOMER_STRING_FIELD_1);
        assertThat(targetMock.getCustomStringField2()).isEqualTo(CUSTOMER_STRING_FIELD_2);
        assertThat(targetMock.getCustomStringField3()).isEqualTo(CUSTOMER_STRING_FIELD_3);
        assertThat(targetMock.getCustomStringField4()).isEqualTo(CUSTOMER_STRING_FIELD_4);
        assertThat(targetMock.getCustomStringField5()).isEqualTo(CUSTOMER_STRING_FIELD_5);
        assertThat(targetMock.getCustomStringField6()).isEqualTo(CUSTOMER_STRING_FIELD_6);
        assertThat(targetMock.getCustomStringField7()).isEqualTo(CUSTOMER_STRING_FIELD_7);
        assertThat(targetMock.getCustomStringField8()).isEqualTo(CUSTOMER_STRING_FIELD_8);
        assertThat(targetMock.getCustomStringField9()).isEqualTo(CUSTOMER_STRING_FIELD_9);
        assertThat(targetMock.getCustomStringField10()).isEqualTo(CUSTOMER_STRING_FIELD_10);

    }
}
