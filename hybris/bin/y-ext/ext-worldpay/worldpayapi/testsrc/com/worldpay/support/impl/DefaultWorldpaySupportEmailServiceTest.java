package com.worldpay.support.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpaySupportEmailServiceTest {

    @Spy
    @InjectMocks
    private DefaultWorldpaySupportEmailService testObj;

    @Mock
    private WorldpaySupportEmailAppender worldpaySupportEmailAppenderMock1, worldpaySupportEmailAppenderMock2, worldpaySupportEmailAppenderMock3;

    @Test
    public void createEmailBodyShouldAppendContentFromAppenders() {
        when(testObj.getEmailAppenders()).thenReturn(Arrays.asList(worldpaySupportEmailAppenderMock1, worldpaySupportEmailAppenderMock2, worldpaySupportEmailAppenderMock3));

        testObj.createEmailBody();


        verify(worldpaySupportEmailAppenderMock1).appendContent();
        verify(worldpaySupportEmailAppenderMock2).appendContent();
        verify(worldpaySupportEmailAppenderMock3).appendContent();
    }
}
