package com.worldpay.worldpayextocc.controllers;

import com.worldpay.config.merchant.GooglePayConfigData;
import com.worldpay.data.GooglePayAdditionalAuthInfo;
import com.worldpay.data.GooglePayAddressData;
import com.worldpay.data.GooglePayAuthorisationRequest;
import com.worldpay.dto.order.PlaceOrderResponseWsDTO;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.payment.DirectResponseData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayGooglePayControllerTest {

    private static final String COUNTRY_CODE = "US";
    private static final String REGION_CODE = "CA";

    @Spy
    @InjectMocks
    public WorldpayGooglePayController testObj;

    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private I18NFacade i18NFacadeMock;
    @Mock
    private UserFacade userFacadeMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private GooglePayConfigData googlePaySettingsMock;
    @Mock
    private HttpServletResponse responseMock;

    @Mock
    private CustomerModel customer;
    @Mock
    private PlaceOrderResponseWsDTO placeOrderResponseMock;
    @Mock
    private DirectResponseData directResponseMock;
    @Mock
    private AddressData addressDataMock;
    @Mock
    private RegionData regionDataMock;

    private final GooglePayAddressData googlePayAddressData = new GooglePayAddressData();

    @Before
    public void setup() throws WorldpayConfigurationException {
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData().getGooglePaySettings()).thenReturn(googlePaySettingsMock);
        when(checkoutCustomerStrategyMock.getCurrentUserForCheckout()).thenReturn(customer);

        doReturn(placeOrderResponseMock)
            .when(testObj)
                .callSuperHandleDirectResponse(directResponseMock, responseMock, FieldSetLevelHelper.DEFAULT_LEVEL);
        when(i18NFacadeMock.getRegion(COUNTRY_CODE, REGION_CODE)).thenReturn(regionDataMock);

        googlePayAddressData.setCountryCode(COUNTRY_CODE);
        googlePayAddressData.setAdministrativeArea(REGION_CODE);
    }

    @Test
    public void getGooglePaySettings_shouldReturnSiteSettings() {
        final ResponseEntity<GooglePayConfigData> result = testObj.getGooglePaySettings();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(googlePaySettingsMock);
    }

    @Test
    public void authoriseOrder_shouldPopulateBillingAddressAndAuthoriseOrder() throws WorldpayException, InvalidCartException {
        final GooglePayAuthorisationRequest authorisationRequest = new GooglePayAuthorisationRequest();
        final GooglePayAddressData billingAddress = createGooglePayAddressData();
        final GooglePayAdditionalAuthInfo token = createGooglePayAdditionalAuthInfo(authorisationRequest);
        authorisationRequest.setBillingAddress(billingAddress);

        when(worldpayDirectOrderFacadeMock.authoriseGooglePayDirect(token)).thenReturn(directResponseMock);

        testObj.authoriseOrder(authorisationRequest, FieldSetLevelHelper.DEFAULT_LEVEL, responseMock);

        verify(worldpayDirectOrderFacadeMock).authoriseGooglePayDirect(authorisationRequest.getToken());

        assertTrue(token.getSaveCard());
    }

    @Test
    public void testSetRegion() {
        testObj.setRegion(addressDataMock, googlePayAddressData);

        verify(addressDataMock).setRegion(regionDataMock);
    }

    @Test
    public void testSetRegion_whenAdministrativeAreaIsEmpty() {
        googlePayAddressData.setAdministrativeArea("");

        testObj.setRegion(addressDataMock, googlePayAddressData);

        verifyNoInteractions(i18NFacadeMock);
        verify(addressDataMock, never()).setRegion(regionDataMock);
    }

    @Test
    public void testSetRegionNotFound() {
        when(i18NFacadeMock.getRegion(COUNTRY_CODE, REGION_CODE)).thenReturn(null);

        testObj.setRegion(addressDataMock, googlePayAddressData);

        verifyNoInteractions(addressDataMock);
    }

    private GooglePayAddressData createGooglePayAddressData() {
        final GooglePayAddressData billingAddress = new GooglePayAddressData();
        billingAddress.setAddress1("Av Aragon 30");
        billingAddress.setAddress2("Planta 8 oficina 999");
        billingAddress.setLocality("Valencia");
        billingAddress.setAdministrativeArea("CA");
        billingAddress.setCountryCode("US");
        billingAddress.setName("James Bond");
        billingAddress.setPostalCode("46015");
        return billingAddress;
    }

    private GooglePayAdditionalAuthInfo createGooglePayAdditionalAuthInfo(final GooglePayAuthorisationRequest authorisationRequest) {
        final GooglePayAdditionalAuthInfo token = new GooglePayAdditionalAuthInfo();
        token.setProtocolVersion("2");
        token.setSignature("lorem ipsum dolor");
        token.setSignedMessage("wop wop wop");
        authorisationRequest.setToken(token);
        authorisationRequest.setSaved(Boolean.TRUE);
        return token;
    }
}

