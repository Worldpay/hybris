package com.worldpay.interceptors.beforeview;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayViewChangeBeforeViewHandlerTest {

    private static final String VIEW_VALUE = "view value";
    private static final String VIEW_NAME = "viewName";

    @InjectMocks
    private WorldpayViewChangeBeforeViewHandler testObj;

    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private ModelMap modelMock;

    private Map<String, String> viewMap = new HashMap<>();

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(testObj, "viewMap", viewMap);
    }

    @Test
    public void beforeView_WhenMapContainsTheView_ShouldGetTheViewValue() {
        viewMap.put(VIEW_NAME, VIEW_VALUE);

        final String result = testObj.beforeView(requestMock, responseMock, modelMock, VIEW_NAME);

        assertThat(result).isEqualTo(VIEW_VALUE);
    }

    @Test
    public void beforeView_WhenMapDoesNotContainsTheView_ShouldGetTheViewName() {
        final String result = testObj.beforeView(requestMock, responseMock, modelMock, VIEW_NAME);

        assertThat(result).isEqualTo(VIEW_NAME);
    }
}
