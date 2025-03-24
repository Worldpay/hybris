package com.worldpay.service.payment.impl;

import com.worldpay.data.ExemptionResponseInfo;
import com.worldpay.data.PaymentReply;
import com.worldpay.data.Request3DInfo;
import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayExemptionServiceTest {

    protected static final String EXEMPTION_RESPONSE_HONOURED = "HONOURED";
    protected static final String EXEMPTION_RESPONSE_OUT_OF_SCOPE = "OUT_OF_SCOPE";
    protected static final String LAST_EVENT_AUTHORISED = "AUTHORISED";

    @InjectMocks
    private DefaultWorldpayExemptionService worldpayExemptionService;
    @Mock
    private ExemptionResponseInfo exemptionResponseInfoMock;
    @Mock
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock;
    @Mock
    private PaymentReply paymentMock;
    @Mock
    private AuthorisedStatus authorisedStatusMock;
    @Mock
    private Request3DInfo request3DInfoMock;

    @Before
    public void setUp() {
        when(directAuthoriseServiceResponseMock.getExemptionResponseInfo()).thenReturn(exemptionResponseInfoMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentMock);
        when(paymentMock.getAuthStatus()).thenReturn(authorisedStatusMock);
        when(authorisedStatusMock.name()).thenReturn(LAST_EVENT_AUTHORISED);
        when(exemptionResponseInfoMock.getResult()).thenReturn(EXEMPTION_RESPONSE_HONOURED);
    }

    @Test
    public void isExemptionHonoured_WhenExemptionResponseIsHonoured_ShouldReturnTrue() {
        when(exemptionResponseInfoMock.getResult()).thenReturn(EXEMPTION_RESPONSE_HONOURED);

        assertTrue(worldpayExemptionService.isExemptionHonoured(exemptionResponseInfoMock));
    }

    @Test
    public void isExemptionHonoured_WhenExemptionResponseIsNotHonoured_ShouldReturnFalse() {
        when(exemptionResponseInfoMock.getResult()).thenReturn(EXEMPTION_RESPONSE_OUT_OF_SCOPE);

        assertFalse(worldpayExemptionService.isExemptionHonoured(exemptionResponseInfoMock));
    }

    @Test
    public void isExemptionHonoured_WhenExemptionResponseIsNull_ShouldReturnFalse() {
        assertFalse(worldpayExemptionService.isExemptionHonoured(null));
    }

    @Test
    public void isRequestToBeRetried_WhenExemptionResponseIsMissingFromReply_ShouldReturnFalse() {
        when(directAuthoriseServiceResponseMock.getExemptionResponseInfo()).thenReturn(null);

        assertFalse(worldpayExemptionService.isRequestWithExemptionToBeRetriedWithoutExemption(directAuthoriseServiceResponseMock));
        verify(paymentMock).getAuthStatus();
    }

    @Test
    public void isRequestToBeRetried_WhenExemptionIsNotHonouredAndLastEventIsNotAuthorised_ShouldReturnTrue() {
        when(exemptionResponseInfoMock.getResult()).thenReturn(EXEMPTION_RESPONSE_OUT_OF_SCOPE);
        when(authorisedStatusMock.name()).thenReturn(null);

        assertTrue(worldpayExemptionService.isRequestWithExemptionToBeRetriedWithoutExemption(directAuthoriseServiceResponseMock));
        verify(paymentMock).getAuthStatus();
    }

    @Test
    public void isRequestToBeRetried_WhenExemptionIsNotHonouredButLastEventWasAuthorised_ShouldReturnFalse() {
        when(exemptionResponseInfoMock.getResult()).thenReturn(EXEMPTION_RESPONSE_OUT_OF_SCOPE);

        assertFalse(worldpayExemptionService.isRequestWithExemptionToBeRetriedWithoutExemption(directAuthoriseServiceResponseMock));
        verify(paymentMock).getAuthStatus();
    }

    @Test
    public void isRequestToBeRetried_WhenResponseRequires3DSChallenge_ShouldReturnFalse() {
        when(exemptionResponseInfoMock.getResult()).thenReturn(EXEMPTION_RESPONSE_OUT_OF_SCOPE);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(request3DInfoMock);

        assertFalse(worldpayExemptionService.isRequestWithExemptionToBeRetriedWithoutExemption(directAuthoriseServiceResponseMock));
        verify(paymentMock, never()).getAuthStatus();
    }

}
