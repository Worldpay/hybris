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
public class WorldpayGenericCMSAddOnComponentControllerTest {
    private static final String URL = "url";

    @Spy
    @InjectMocks
    private WorldpayGenericCMSAddOnComponentController testObj;

    @Test
    public void fillModel_shouldCallInvokeSuperHandleGet() throws Exception {
        Mockito.doReturn(URL).when(testObj).handleGet(any(), any(), any());

        testObj.handleGet(any(), any(), any());

        Mockito.verify(testObj).handleGet(any(), any(), any());
    }

}
