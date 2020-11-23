package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.facades.WorldpayDirectResponseFacade;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import com.worldpay.facades.payment.direct.WorldpayDDCFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.WorldpayAddonEndpointService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;

import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractWorldpayDirectCheckoutStepControllerTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String RETURN_CODE = "A12";
    private static final String LOCALISED_DECLINE_MESSAGE = "localisedDeclineMessage";
    private static final String ERROR_VIEW = "error";
    private static final String AUTOSUBMIT_3DSECURE = "AutoSubmit3DSecure";
    private static final String AUTOSUBMIT_3DSECURE_FLEX = "AutoSubmit3dSecureFlex";
    private static final String MERCHANT_DATA_VALUE = "merchantDataValue";
    private static final String THREEDSFLEX_JSON_WEB_TOKEN_VALUE = "THREEDSFLEX_JSON_WEB_TOKEN_VALUE";
    private static final String THREEDSECURE_FLEX_DDC_URL_VALUE = "threeDSecureDDCUrlValue";
    private static final String THREEDSECURE_JWT_FLEX_DDC = "jwt3DSecureFlexDDC";
    private static final String THREEDSECURE_FLEX_DDC_URL = "threeDSecureDDCUrl";

    @Spy
    @InjectMocks
    private final AbstractWorldpayDirectCheckoutStepController testObj = new TestAbstractWorldpayDirectCheckoutStepController();

    @Mock
    private WorldpayDDCFacade worldpayDDCFacadeMock;
    @Mock
    private WorldpayAddonEndpointService worldpayAddonEndpointServiceMock;
    @Mock
    private WorldpayDirectResponseFacade worldpayDirectResponseFacadeMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;

    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private Model modelMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private WorldpayCheckoutFacadeDecorator checkoutFacadeMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;

    @Before
    public void setUp() {
        doReturn(LOCALISED_DECLINE_MESSAGE).when(testObj).getLocalisedDeclineMessage(RETURN_CODE);
        when(directResponseDataMock.getTransactionStatus()).thenReturn(AUTHORISED);
        when(directResponseDataMock.getOrderData()).thenReturn(orderDataMock);
        when(directResponseDataMock.getReturnCode()).thenReturn(RETURN_CODE);
        when(orderDataMock.getCode()).thenReturn(ORDER_CODE);
        when(cartDataMock.getWorldpayOrderCode()).thenReturn(MERCHANT_DATA_VALUE);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
        when(worldpayAddonEndpointServiceMock.getAutoSubmit3DSecure()).thenReturn(AUTOSUBMIT_3DSECURE);
        when(worldpayAddonEndpointServiceMock.getAutoSubmit3DSecureFlex()).thenReturn(AUTOSUBMIT_3DSECURE_FLEX);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData()).thenReturn(worldpayMerchantConfigDataMock);
    }

    @Test
    public void shouldRedirectTo3DSecureIfAuthenticationRequiredForLegacyFlow() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(worldpayDirectResponseFacadeMock.is3DSecureLegacyFlow(directResponseDataMock)).thenReturn(true);

        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        assertEquals(AUTOSUBMIT_3DSECURE, result);
    }

    @Test
    public void shouldRedirectTo3DSecureIfAuthenticationRequiredFor3DSecureFlexFlow() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(worldpayDirectResponseFacadeMock.is3DSecureFlexFlow(directResponseDataMock)).thenReturn(true);

        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        assertEquals(AUTOSUBMIT_3DSECURE_FLEX, result);
    }

    @Test
    public void shouldRetrieveAllAttributesForModelIfPaymentRequires3dSecureLegacyFlowAuthentication() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(worldpayDirectResponseFacadeMock.is3DSecureLegacyFlow(directResponseDataMock)).thenReturn(true);

        testObj.handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        verify(worldpayDirectResponseFacadeMock).retrieveAttributesForLegacy3dSecure(directResponseDataMock);
    }

    @Test
    public void shouldRetrieveAllAttributesForModelIfPaymentRequires3dSecureFlexFlowAuthentication() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(worldpayDirectResponseFacadeMock.is3DSecureFlexFlow(directResponseDataMock)).thenReturn(true);

        testObj.handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        verify(worldpayDirectResponseFacadeMock).retrieveAttributesForFlex3dSecure(directResponseDataMock);
    }

    @Test
    public void shouldReturnErrorPageWhenCancelled() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(worldpayDirectResponseFacadeMock.isCancelled(directResponseDataMock)).thenReturn(true);

        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        assertEquals(ERROR_VIEW, result);
    }

    @Test
    public void shouldRedirectToErrorIfResponseIsNotAuthorisedOrAuthRequired() throws CMSItemNotFoundException, WorldpayConfigurationException {
        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock, responseMock);

        assertEquals(ERROR_VIEW, result);
    }

    @Test
    public void setDDCIframeData_shouldPopulate3DSecureJsonWebToken_AndDDCURLValue() {
        when(worldpayDDCFacadeMock.createJsonWebTokenForDDC()).thenReturn(THREEDSFLEX_JSON_WEB_TOKEN_VALUE);
        when(worldpayMerchantConfigDataMock.getThreeDSFlexJsonWebTokenSettings().getDdcUrl()).thenReturn(THREEDSECURE_FLEX_DDC_URL_VALUE);

        testObj.setDDCIframeData(modelMock);

        verify(modelMock).addAttribute(THREEDSECURE_JWT_FLEX_DDC, THREEDSFLEX_JSON_WEB_TOKEN_VALUE);
        verify(modelMock).addAttribute(THREEDSECURE_FLEX_DDC_URL, THREEDSECURE_FLEX_DDC_URL_VALUE);

    }

    public class TestAbstractWorldpayDirectCheckoutStepController extends AbstractWorldpayDirectCheckoutStepController {
        @Override
        protected String getErrorView(final Model model) {
            return ERROR_VIEW;
        }
    }
}
