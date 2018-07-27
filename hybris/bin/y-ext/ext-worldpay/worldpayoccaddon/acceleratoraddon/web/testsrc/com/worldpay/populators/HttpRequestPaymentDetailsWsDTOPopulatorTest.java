package com.worldpay.populators;

import com.worldpay.populator.options.PaymentDetailsWsDTOOption;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class HttpRequestPaymentDetailsWsDTOPopulatorTest {

    private static final String VISA = "visa";
    private static final String ACCOUNT_HOLDER_NAME = "accountHolderName";
    private static final String CARD_NUMBER = "cardNumber";
    private static final String CARD_TYPE = "cardType";
    private static final String CSE_TOKEN = "cseToken";
    private static final String EXPIRY_MONTH = "expiryMonth";
    private static final String EXPIRY_YEAR = "expiryYear";
    private static final String ISSUE_NUMBER = "issueNumber";
    private static final String START_MONTH = "startMonth";
    private static final String START_YEAR = "startYear";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final String ID = "id";

    @InjectMocks
    private HttpRequestPaymentDetailsWsDTOPopulator testObject;

    @Mock
    private HttpRequestAddressWsDTOPopulator httpRequestAddressWsDTOPopulatorMock;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    private PaymentDetailsWsDTO paymentDetails = new PaymentDetailsWsDTO();
    private Collection<PaymentDetailsWsDTOOption> options = new ArrayList<>();


    @Before
    public void setUp() {
        options.add(PaymentDetailsWsDTOOption.BASIC);
        when(httpServletRequestMock.getParameter(ACCOUNT_HOLDER_NAME)).thenReturn(ACCOUNT_HOLDER_NAME);
        when(httpServletRequestMock.getParameter(CARD_NUMBER)).thenReturn(CARD_NUMBER);
        when(httpServletRequestMock.getParameter(CSE_TOKEN)).thenReturn(CSE_TOKEN);
        when(httpServletRequestMock.getParameter(EXPIRY_MONTH)).thenReturn(EXPIRY_MONTH);
        when(httpServletRequestMock.getParameter(EXPIRY_YEAR)).thenReturn(EXPIRY_YEAR);
        when(httpServletRequestMock.getParameter(SUBSCRIPTION_ID)).thenReturn(SUBSCRIPTION_ID);
        when(httpServletRequestMock.getParameter(ID)).thenReturn(ID);
        when(httpServletRequestMock.getParameter(ISSUE_NUMBER)).thenReturn(ISSUE_NUMBER);
        when(httpServletRequestMock.getParameter(START_MONTH)).thenReturn(START_MONTH);
        when(httpServletRequestMock.getParameter(START_YEAR)).thenReturn(START_YEAR);
        when(httpServletRequestMock.getParameter(CARD_TYPE)).thenReturn(VISA);
    }

    @Test
    public void testBasicPopulationOfBasicFields() {
        // Setup
        // Execute
        testObject.populate(httpServletRequestMock, paymentDetails, options);

        // Verify
        verifyBasic(paymentDetails);
        assertNull("Expect no billing address", paymentDetails.getBillingAddress());
    }

    @Test
    public void testBasicPopulationOfAllFields() {
        // Setup
        options.add(PaymentDetailsWsDTOOption.BILLING_ADDRESS);
        // Execute
        testObject.populate(httpServletRequestMock, paymentDetails, options);

        // Verify
        verifyBasic(paymentDetails);
        verify(httpRequestAddressWsDTOPopulatorMock).populate(httpServletRequestMock, paymentDetails.getBillingAddress());
        verify(httpRequestAddressWsDTOPopulatorMock).setAddressPrefix("billingAddress");
        assertNotNull("Expect billing address", paymentDetails.getBillingAddress());
    }

    private void verifyBasic(final PaymentDetailsWsDTO paymentDetails) {
        assertEquals(ACCOUNT_HOLDER_NAME, paymentDetails.getAccountHolderName());
        assertEquals(CARD_NUMBER, paymentDetails.getCardNumber());
        assertEquals(CSE_TOKEN, paymentDetails.getCseToken());
        assertEquals(EXPIRY_MONTH, paymentDetails.getExpiryMonth());
        assertEquals(EXPIRY_YEAR, paymentDetails.getExpiryYear());
        assertEquals(ID, paymentDetails.getId());
        assertEquals(ISSUE_NUMBER, paymentDetails.getIssueNumber());
        assertEquals(START_MONTH, paymentDetails.getStartMonth());
        assertEquals(START_YEAR, paymentDetails.getStartYear());
        assertEquals(SUBSCRIPTION_ID, paymentDetails.getSubscriptionId());

        assertEquals(VISA, paymentDetails.getCardType().getCode());
        assertFalse(paymentDetails.getSaved());
        assertFalse(paymentDetails.getDefaultPayment());
    }
}
