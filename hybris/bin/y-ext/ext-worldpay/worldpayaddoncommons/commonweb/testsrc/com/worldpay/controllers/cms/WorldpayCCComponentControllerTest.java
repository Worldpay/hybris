package com.worldpay.controllers.cms;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCCComponentControllerTest {

    @Spy
    @InjectMocks
    private WorldpayCCComponentController testObj;

    @Test
    public void fillModel_shouldCallInvokeSuperFillModel() {
        Mockito.doNothing().when(testObj).invokeSuperFillModel(any(), any(), any());

        testObj.fillModel(any(), any(), any());

        Mockito.verify(testObj).invokeSuperFillModel(any(), any(), any());
    }

}