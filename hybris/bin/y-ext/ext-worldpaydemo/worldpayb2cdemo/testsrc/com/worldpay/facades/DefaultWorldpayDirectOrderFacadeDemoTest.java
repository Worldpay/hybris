package com.worldpay.facades;

import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.model.IPInformationModel;
import com.worldpay.model.WeatherInformationModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.service.IPLookupService;
import com.worldpay.service.WeatherLookupService;
import com.worldpay.data.PaymentReply;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayDirectOrderFacadeDemoTest {

    private static final String CUSTOMER_IP_ADDRESS = "2.3.4.5";
    private static final String LAT = "111";
    private static final String LON = "222";

    @Spy
    @InjectMocks
    private DefaultWorldpayDirectOrderFacadeDemo testObj;

    @Mock
    private IPLookupService ipLookupServiceMock;
    @Mock
    private WeatherLookupService weatherLookupServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;

    @Mock
    private CartModel cartModelMock;
    @Mock
    private IPInformationModel ipInformationModelMock;
    @Mock
    private WeatherInformationModel weatherInfoMock;
    @Mock
    private DirectAuthoriseServiceResponse serviceResponseMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoData;
    @Mock
    private UserModel userModelMock;
    @Mock
    private DirectResponseData directResponseDataMock;

    @Before
    public void setUp() throws Exception {
        when(worldpayAdditionalInfoData.getCustomerIPAddress()).thenReturn(CUSTOMER_IP_ADDRESS);
        when(cartServiceMock.hasSessionCart()).thenReturn(true);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(ipLookupServiceMock.getIPInformation(CUSTOMER_IP_ADDRESS)).thenReturn(ipInformationModelMock);
        when(ipInformationModelMock.getLon()).thenReturn(LON);
        when(ipInformationModelMock.getLat()).thenReturn(LAT);
        when(weatherLookupServiceMock.getWeatherInformation(LAT, LON)).thenReturn(weatherInfoMock);

        when(worldpayDirectOrderFacadeMock.authorise(worldpayAdditionalInfoData)).thenReturn(directResponseDataMock);

    }

    @Test
    public void authorise_shouldPopulateWeatherAndIPInformation() throws Exception {

        final DirectResponseData result = testObj.authorise(worldpayAdditionalInfoData);

        verify(cartModelMock).setIpInformation(ipInformationModelMock);
        verify(cartModelMock).setWeatherInformation(weatherInfoMock);

        assertThat(result).isEqualTo(directResponseDataMock);
    }

    @Test
    public void authorise_shouldNotPopulateWeatherInformation_WhenLatitudeIsNull() throws Exception {
        when(ipInformationModelMock.getLat()).thenReturn(null);

        final DirectResponseData result = testObj.authorise(worldpayAdditionalInfoData);

        verifyNoInteractions(weatherLookupServiceMock);
        verify(cartModelMock, never()).setWeatherInformation(weatherInfoMock);

        assertThat(result).isEqualTo(directResponseDataMock);
    }

    @Test
    public void authorise_shouldNotPopulateWeatherInformation_WhenLongitudeIsNull() throws Exception {
        when(ipInformationModelMock.getLon()).thenReturn(null);

        final DirectResponseData result = testObj.authorise(worldpayAdditionalInfoData);

        verifyNoInteractions(weatherLookupServiceMock);
        verify(cartModelMock, never()).setWeatherInformation(weatherInfoMock);

        assertThat(result).isEqualTo(directResponseDataMock);
    }
}
