package com.worldpay.worldpayextocc.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractHttpRequestWsDTOPopulatorTest {

    private static final String PARAM_VALUE = "value";
    private static final String DEFAULT_VALUE = "default value";
    private static final String PARAM_NAME = "param name";

    private AbstractHttpRequestWsDTOPopulator testObj;

    @Mock
    private HttpServletRequest requestMock;

    @Before
    public void setUp() {
        testObj = Mockito.mock(
            AbstractHttpRequestWsDTOPopulator.class,
            Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void updateStringValueFromRequest_WhenRequestParameterValueIsBlank_ShouldReturnNull() {
        when(requestMock.getParameter(PARAM_NAME)).thenReturn("");

        final String result = testObj.updateStringValueFromRequest(requestMock, PARAM_NAME, DEFAULT_VALUE);

        assertThat(result).isNull();
    }

    @Test
    public void updateStringValueFromRequest_WhenRequestParameterValueIsNotBlank_ShouldReturnTheValue() {
        when(requestMock.getParameter(PARAM_NAME)).thenReturn(PARAM_VALUE);

        final String result = testObj.updateStringValueFromRequest(requestMock, PARAM_NAME, DEFAULT_VALUE);

        assertThat(result).isEqualTo(PARAM_VALUE);
    }

    @Test
    public void updateStringValueFromRequest_WhenRequestParameterValueIsNull_ShouldReturnTheDefaultValue() {
        final String result = testObj.updateStringValueFromRequest(requestMock, PARAM_NAME, DEFAULT_VALUE);

        assertThat(result).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    public void updateBooleanValueFromRequest_WhenRequestParameterValueIsBlank_ShouldTheDefaultValue() {
        when(requestMock.getParameter(PARAM_NAME)).thenReturn("");

        final boolean result = testObj.updateBooleanValueFromRequest(requestMock, PARAM_NAME, true);

        assertThat(result).isTrue();
    }

    @Test
    public void updateBooleanValueFromRequest_WhenRequestParameterValueIsNotBlank_ShouldReturnTheValue() {
        when(requestMock.getParameter(PARAM_NAME)).thenReturn("false");

        final boolean result = testObj.updateBooleanValueFromRequest(requestMock, PARAM_NAME, true);

        assertThat(result).isFalse();
    }

    @Test
    public void updateBooleanValueFromRequest_WhenRequestParameterValueIsNull_ShouldReturnTheDefaultValue() {
        final boolean result = testObj.updateBooleanValueFromRequest(requestMock, PARAM_NAME, true);

        assertThat(result).isTrue();
    }
}
