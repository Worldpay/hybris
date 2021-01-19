package com.worldpay.controllers;

import com.worldpay.data.ApplePayAdditionalAuthInfo;
import com.worldpay.data.ApplePayAuthorisationRequest;
import com.worldpay.data.ApplePayPaymentContact;
import com.worldpay.dto.applepay.ValidateMerchantRequestWsDTO;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayApplePayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.payment.applepay.ValidateMerchantRequestDTO;
import com.worldpay.payment.applepay.ValidateMerchantRequestData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ApplePayControllerTest {

    @InjectMocks
    private ApplePayController testObj;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private RestTemplate restTemplateMock;
    @Mock
    private BaseSiteService baseSiteServiceMock;
    @Mock
    private SiteBaseUrlResolutionService siteBaseUrlResolutionServiceMock;
    @Mock
    private CustomerModel customerMock;
    @Mock
    private BaseSiteModel currentBaseSiteMock;
    @Mock
    private ValidateMerchantRequestData validateMerchantRequestDataMock;
    @Mock
    private DataMapper dataMapperMock;
    @Mock
    private ValidateMerchantRequestWsDTO validateMerchantRequestWsDTOMock;
    @Mock
    private WorldpayApplePayPaymentCheckoutFacade worldpayApplePayPaymentCheckoutFacadeMock;
    @Mock
    private ValidateMerchantRequestDTO validateMerchantRequestDTOMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ApplePayAuthorisationRequest authorisationRequestMock;
    @Mock
    private ApplePayPaymentContact billingContactMock;
    @Mock
    private ApplePayAdditionalAuthInfo paymentDataMock;


    @Before
    public void setup() {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(currentBaseSiteMock);
        when(siteBaseUrlResolutionServiceMock.getWebsiteUrlForSite(currentBaseSiteMock, true, null)).thenReturn("https://electronics.ypay-daily-responsive.e2y.io/worlpaystorefront/en");
        when(checkoutCustomerStrategyMock.getCurrentUserForCheckout()).thenReturn(customerMock);
    }

    @Test
    public void testAuthoriseOrder() throws WorldpayException, InvalidCartException {
        doNothing().when(worldpayApplePayPaymentCheckoutFacadeMock).saveBillingAddresses(billingContactMock);
        when(authorisationRequestMock.getBillingContact()).thenReturn(billingContactMock);
        when(authorisationRequestMock.getToken().getPaymentData()).thenReturn(paymentDataMock);

        testObj.authoriseOrder(authorisationRequestMock);

        verify(worldpayApplePayPaymentCheckoutFacadeMock).saveBillingAddresses(billingContactMock);
        verify(worldpayDirectOrderFacadeMock).authoriseApplePayDirect(paymentDataMock);
    }

    @Test
    public void testRequestPaymentSession() {
        when(dataMapperMock.map(validateMerchantRequestWsDTOMock, ValidateMerchantRequestData.class)).thenReturn(validateMerchantRequestDataMock);
        when(validateMerchantRequestDataMock.getValidationURL()).thenReturn("http://apple.com");
        when(worldpayApplePayPaymentCheckoutFacadeMock.getValidateMerchantRequestDTO()).thenReturn(validateMerchantRequestDTOMock);

        testObj.requestPaymentSession(validateMerchantRequestWsDTOMock);

        verify(worldpayApplePayPaymentCheckoutFacadeMock).getValidateMerchantRequestDTO();
        verify(restTemplateMock).postForObject(validateMerchantRequestDataMock.getValidationURL(), validateMerchantRequestDTOMock, Object.class);
    }
}
