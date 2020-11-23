package com.worldpay.support.appender.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCurrentTimeAppenderTest {

    @InjectMocks
    private WorldpayCurrentTimeAppender testObj;

    @Test
    public void appendContent_ShouldAppendCurrentTime() {
        final String result = testObj.appendContent();

        assertTrue(result.contains("Time: "));
    }
}
    

