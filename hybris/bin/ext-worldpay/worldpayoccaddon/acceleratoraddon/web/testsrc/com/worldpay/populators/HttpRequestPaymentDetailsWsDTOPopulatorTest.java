package com.worldpay.populators;

import com.worldpay.populator.options.PaymentDetailsWsDTOOption;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class HttpRequestPaymentDetailsWsDTOPopulatorTest {

    private static final String VISA = "visa";

    @InjectMocks
    private HttpRequestPaymentDetailsWsDTOPopulator testObject;

    @Mock
    @SuppressWarnings("PMD.UnusedPrivateField")
    private HttpRequestAddressWsDTOPopulator httpRequestAddressWsDTOPopulator;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    private PaymentDetailsWsDTO paymentDetails = new PaymentDetailsWsDTO();
    private Collection<PaymentDetailsWsDTOOption> options = new ArrayList<PaymentDetailsWsDTOOption>();


    @Before
    public void setUp() {
        options.add(PaymentDetailsWsDTOOption.BASIC);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestPaymentDetailsWsDTOPopulator.ACCOUNT_HOLDER_NAME)).thenReturn(HttpRequestPaymentDetailsWsDTOPopulator.ACCOUNT_HOLDER_NAME);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestPaymentDetailsWsDTOPopulator.CARD_NUMBER)).thenReturn(HttpRequestPaymentDetailsWsDTOPopulator.CARD_NUMBER);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestPaymentDetailsWsDTOPopulator.CSE_TOKEN)).thenReturn(HttpRequestPaymentDetailsWsDTOPopulator.CSE_TOKEN);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestPaymentDetailsWsDTOPopulator.EXPIRY_MONTH)).thenReturn(HttpRequestPaymentDetailsWsDTOPopulator.EXPIRY_MONTH);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestPaymentDetailsWsDTOPopulator.EXPIRY_YEAR)).thenReturn(HttpRequestPaymentDetailsWsDTOPopulator.EXPIRY_YEAR);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestPaymentDetailsWsDTOPopulator.SUBSCRIPTION_ID)).thenReturn(HttpRequestPaymentDetailsWsDTOPopulator.SUBSCRIPTION_ID);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestPaymentDetailsWsDTOPopulator.ID)).thenReturn(HttpRequestPaymentDetailsWsDTOPopulator.ID);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestPaymentDetailsWsDTOPopulator.ISSUE_NUMBER)).thenReturn(HttpRequestPaymentDetailsWsDTOPopulator.ISSUE_NUMBER);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestPaymentDetailsWsDTOPopulator.START_MONTH)).thenReturn(HttpRequestPaymentDetailsWsDTOPopulator.START_MONTH);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestPaymentDetailsWsDTOPopulator.START_YEAR)).thenReturn(HttpRequestPaymentDetailsWsDTOPopulator.START_YEAR);
        Mockito.when(httpServletRequestMock.getParameter(HttpRequestPaymentDetailsWsDTOPopulator.CARD_TYPE)).thenReturn(VISA);
    }

    @Test
    public void testBasicPopulationOfBasicFields() {
        // Setup
        // Execute
        testObject.populate(httpServletRequestMock, paymentDetails, options);

        // Verify
        verifyBasic(paymentDetails);
        Assert.assertEquals("Expect no billing address", null, paymentDetails.getBillingAddress());
    }

    @Test
    public void testBasicPopulationOfAllFields() {
        // Setup
        options.add(PaymentDetailsWsDTOOption.BILLING_ADDRESS);
        // Execute
        testObject.populate(httpServletRequestMock, paymentDetails, options);

        // Verify
        verifyBasic(paymentDetails);
        Assert.assertNotNull("Expect billing address", paymentDetails.getBillingAddress());
    }

    private void verifyBasic(final PaymentDetailsWsDTO paymentDetails) {
        Assert.assertEquals(HttpRequestPaymentDetailsWsDTOPopulator.ACCOUNT_HOLDER_NAME, paymentDetails.getAccountHolderName());
        Assert.assertEquals(HttpRequestPaymentDetailsWsDTOPopulator.CARD_NUMBER, paymentDetails.getCardNumber());
        Assert.assertEquals(HttpRequestPaymentDetailsWsDTOPopulator.CSE_TOKEN, paymentDetails.getCseToken());
        Assert.assertEquals(HttpRequestPaymentDetailsWsDTOPopulator.EXPIRY_MONTH, paymentDetails.getExpiryMonth());
        Assert.assertEquals(HttpRequestPaymentDetailsWsDTOPopulator.EXPIRY_YEAR, paymentDetails.getExpiryYear());
        Assert.assertEquals(HttpRequestPaymentDetailsWsDTOPopulator.ID, paymentDetails.getId());
        Assert.assertEquals(HttpRequestPaymentDetailsWsDTOPopulator.ISSUE_NUMBER, paymentDetails.getIssueNumber());
        Assert.assertEquals(HttpRequestPaymentDetailsWsDTOPopulator.START_MONTH, paymentDetails.getStartMonth());
        Assert.assertEquals(HttpRequestPaymentDetailsWsDTOPopulator.START_YEAR, paymentDetails.getStartYear());
        Assert.assertEquals(HttpRequestPaymentDetailsWsDTOPopulator.SUBSCRIPTION_ID, paymentDetails.getSubscriptionId());

        Assert.assertEquals(VISA, paymentDetails.getCardType().getCode());
        Assert.assertFalse(paymentDetails.getSaved());
        Assert.assertFalse(paymentDetails.getDefaultPayment());
    }
}
