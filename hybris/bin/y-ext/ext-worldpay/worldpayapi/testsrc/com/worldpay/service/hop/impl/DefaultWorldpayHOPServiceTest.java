package com.worldpay.service.hop.impl;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.service.WorldpayURIService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.data.ErrorDetail;
import com.worldpay.data.MerchantInfo;
import com.worldpay.data.RedirectReference;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayHOPServiceTest {
    private static final String REDIRECT_URL = "http://www.example.com";
    private static final String LANGUAGE_ISO_CODE = "en";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String FULL_SUCCESS_URL = "fullSuccessUrl";
    private static final String FULL_PENDING_URL = "fullPendingUrl";
    private static final String FULL_FAILURE_URL = "fullFailureUrl";
    private static final String FULL_CANCEL_URL = "fullCancelUrl";
    private static final String FULL_ERROR_URL = "fullErrorUrl";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_CANCEL_URL = "cancelURL";
    private static final String KEY_SUCCESS_URL = "successURL";
    private static final String KEY_PENDING_URL = "pendingURL";
    private static final String KEY_FAILURE_URL = "failureURL";
    private static final String WORLDPAY_MERCHANT_CODE = "worldpayMerchantCode";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String LANGUAGE_SESSION_ATTRIBUTE_KEY = "language";


    @InjectMocks
    private DefaultWorldpayHOPService testObj;

    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private WorldpayURIService worldpayURIServiceMock;
    @Mock
    private WorldpayServiceGateway worldpayServiceGatewayMock;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private RedirectAuthoriseServiceRequest redirectAuthoriseServiceRequestMock;
    @Mock
    private RedirectAuthoriseServiceResponse redirectAuthoriseServiceResponseMock;
    @Mock
    private ErrorDetail errorDetailMock;
    @Mock
    private RedirectReference redirectReferenceMock;
    @Mock
    private WorldpayRequestFactory worldpayRequestFactoryMock;
    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;
    @Mock
    private LanguageModel currentSessionLanguageMock;
    @Mock
    private UserModel customerModelMock;
    @Mock
    private AddressModel cartPaymentAddressModelMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;

    @Before
    public void setUp() throws Exception {
        when(worldpayRequestFactoryMock.buildRedirectAuthoriseRequest(merchantInfoMock, cartModelMock, additionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(redirectAuthoriseServiceRequestMock);
        when(worldpayServiceGatewayMock.redirectAuthorise(redirectAuthoriseServiceRequestMock)).thenReturn(redirectAuthoriseServiceResponseMock);
        when(redirectAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(redirectReferenceMock);
        when(redirectAuthoriseServiceRequestMock.getOrder().getBillingAddress().getCountryCode()).thenReturn(COUNTRY_CODE);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(redirectReferenceMock.getValue()).thenReturn(REDIRECT_URL);
        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(FULL_SUCCESS_URL);
        when(worldpayUrlServiceMock.getFullPendingURL()).thenReturn(FULL_PENDING_URL);
        when(worldpayUrlServiceMock.getFullFailureURL()).thenReturn(FULL_FAILURE_URL);
        when(worldpayUrlServiceMock.getFullCancelURL()).thenReturn(FULL_CANCEL_URL);
        when(worldpayUrlServiceMock.getFullErrorURL()).thenReturn(FULL_ERROR_URL);
        when(sessionServiceMock.getAttribute(LANGUAGE_SESSION_ATTRIBUTE_KEY)).thenReturn(currentSessionLanguageMock);
        when(currentSessionLanguageMock.getIsocode()).thenReturn(LANGUAGE_ISO_CODE);

        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(cartModelMock.getPaymentAddress()).thenReturn(cartPaymentAddressModelMock);
    }

    @Test
    public void buildHOPPageData_ShouldReturnPaymentDataCorrectlySet_WhenItIsCall() throws WorldpayException {
        final PaymentData result = testObj.buildHOPPageData(cartModelMock, additionalAuthInfoMock, merchantInfoMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock).setAttribute(WORLDPAY_MERCHANT_CODE, MERCHANT_CODE);
        verify(worldpayURIServiceMock).extractUrlParamsToMap(eq(REDIRECT_URL), anyMapOf(String.class, String.class));
        assertEquals(REDIRECT_URL, result.getPostUrl());
        assertEquals(COUNTRY_CODE, result.getParameters().get(KEY_COUNTRY));
        assertEquals(LANGUAGE_ISO_CODE, result.getParameters().get(LANGUAGE_SESSION_ATTRIBUTE_KEY));
        assertEquals(FULL_SUCCESS_URL, result.getParameters().get(KEY_SUCCESS_URL));
        assertEquals(FULL_PENDING_URL, result.getParameters().get(KEY_PENDING_URL));
        assertEquals(FULL_FAILURE_URL, result.getParameters().get(KEY_FAILURE_URL));
        assertEquals(FULL_CANCEL_URL, result.getParameters().get(KEY_CANCEL_URL));
    }

    @Test(expected = WorldpayException.class)
    public void redirectAuthorise_ShouldThrowAWPException_WhenWorldpayServiceGatewayRiseIt() throws WorldpayException {
        when(worldpayServiceGatewayMock.redirectAuthorise(redirectAuthoriseServiceRequestMock)).thenThrow(new WorldpayException(("Response Error")));

        testObj.buildHOPPageData(cartModelMock, additionalAuthInfoMock, merchantInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test(expected = WorldpayException.class)
    public void redirectAuthorise_ShouldThrowAWPException_WhenWorldpayServiceGatewayResponseIsNull() throws WorldpayException {
        when(worldpayServiceGatewayMock.redirectAuthorise(redirectAuthoriseServiceRequestMock)).thenReturn(null);

        testObj.buildHOPPageData(cartModelMock, additionalAuthInfoMock, merchantInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test(expected = WorldpayException.class)
    public void redirectAuthorise_ShouldThrowAWPException_WhenRedirectReferenceFromRedirectResponseIsNull() throws WorldpayException {
        when(redirectAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(null);
        when(redirectAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.buildHOPPageData(cartModelMock, additionalAuthInfoMock, merchantInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test(expected = WorldpayException.class)
    public void redirectAuthorise_ShouldThrowAWPException_WhenRedirectReferenceValueIsNull() throws WorldpayException {
        when(redirectReferenceMock.getValue()).thenReturn(null);

        testObj.buildHOPPageData(cartModelMock, additionalAuthInfoMock, merchantInfoMock, worldpayAdditionalInfoDataMock);
    }
}
