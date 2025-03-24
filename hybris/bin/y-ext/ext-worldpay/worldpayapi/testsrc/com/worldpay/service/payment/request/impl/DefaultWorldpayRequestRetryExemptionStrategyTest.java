package com.worldpay.service.payment.request.impl;

import com.worldpay.data.Order;
import com.worldpay.service.payment.WorldpayExemptionService;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayRequestRetryExemptionStrategyTest {

    @InjectMocks
    private DefaultWorldpayRequestRetryExemptionStrategy testObj;

    @Mock
    private WorldpayExemptionService worldpayExemptionService;
    @Mock
    private DirectAuthoriseServiceRequest requestMock;
    @Mock
    private DirectAuthoriseServiceResponse responseMock;
    @Mock
    private Order orderMock;

    @Test
    public void isRequestToBeRetried_shouldReturnTrue_WhenResponseContainsNoErrorsAndIsToBeRetriedAccordingToExemptionService() {
        when(worldpayExemptionService.isRequestWithExemptionToBeRetriedWithoutExemption(responseMock)).thenReturn(true);

        assertTrue(testObj.isRequestToBeRetried(requestMock, responseMock));
    }

    @Test
    public void isRequestToBeRetried_shouldReturnFalse_WhenResponseContainsNoErrorsAndIsNotToBeRetriedAccordingToExemptionService() {
        assertFalse(testObj.isRequestToBeRetried(requestMock, responseMock));
    }

    @Test
    public void isRequestToBeRetried_shouldReturnFalse_WhenResponseContainsErrors() {
        when(responseMock.isError()).thenReturn(true);

        assertFalse(testObj.isRequestToBeRetried(requestMock, responseMock));
    }

    @Test
    public void getDirectAuthoriseServiceRequestToRetry_shouldSetExemptionToNull_whenInvoked() {
        when(requestMock.getOrder()).thenReturn(orderMock);

        testObj.getDirectAuthoriseServiceRequestToRetry(requestMock, responseMock);

        verify(orderMock).setExemption(null);
    }

}
