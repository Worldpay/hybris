package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.model.WorldpayFraudSightModel;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.data.Address;
import com.worldpay.data.FraudSightData;
import com.worldpay.data.FraudSightResponse;
import com.worldpay.data.PaymentReply;
import com.worldpay.service.request.AuthoriseRequestParameters;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayFraudSightStrategyTest {

    private static final String ADDRESS_LINE_2 = "Address Line 2";
    private static final String SHOPPER_ID = "shopper_id";
    private static final String CUSTOMER_NAME = "Customer Name";
    private static final Date BIRTHDAY_DATE = new Date(1990, Calendar.MAY, 17);
    private static final String DEVICE_SESSION = "deviceSession";

    @Spy
    @InjectMocks
    private DefaultWorldpayFraudSightStrategy testObj;

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private Converter<AddressModel, Address> worldpayAddressConverterMock;
    @Mock
    private Converter<FraudSightResponse, WorldpayFraudSightModel> worldpayFraudSightResponseConverterMock;
    @Mock
    private CartModel cartMock;
    @Mock
    private CMSSiteModel siteMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private CustomerModel customerMock;
    @Mock
    private BaseSiteService baseSiteServiceMock;
    @Mock
    private AddressModel addressMock;
    @Mock
    private Address convertedAddressMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private FraudSightResponse fraudSightMock;
    @Mock
    private WorldpayFraudSightModel worldpayFrauSightMock;
    @Mock
    private FraudSightData fraudSightDataMock;
    @Mock
    private AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreatorMock;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(testObj, "worldpayAddressConverter", worldpayAddressConverterMock);
        ReflectionTestUtils.setField(testObj, "worldpayFraudSightResponseConverter", worldpayFraudSightResponseConverterMock);

        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(siteMock);
        when(worldpayFraudSightResponseConverterMock.convert(fraudSightMock)).thenReturn(worldpayFrauSightMock);
    }

    @Test
    public void isFraudSightEnabled_WhenFSIsEnabledForSite_ShouldReturnTrue() {
        when(siteMock.getEnableFS()).thenReturn(true);

        final boolean result = testObj.isFraudSightEnabled();

        assertThat(result).isTrue();
    }

    @Test
    public void isFraudSightEnabled_WhenFSIsDisabledForSite_ShouldReturnFalse() {
        when(siteMock.getEnableFS()).thenReturn(false);

        final boolean result = testObj.isFraudSightEnabled();

        assertThat(result).isFalse();
    }

    @Test
    public void isFraudSightEnabled_WhenFSIsEnabledForGivenSite_ShouldReturnTrue() {
        when(siteMock.getEnableFS()).thenReturn(true);

        final boolean result = testObj.isFraudSightEnabled(siteMock);

        assertThat(result).isTrue();
    }

    @Test
    public void isFraudSightEnabled_WhenFSIsDisabledForGivenSite_ShouldReturnFalse() {
        when(siteMock.getEnableFS()).thenReturn(false);

        final boolean result = testObj.isFraudSightEnabled(siteMock);

        assertThat(result).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFraudSightData_WhenCartIsNull_ShouldThrowException() {
        testObj.createFraudSightData(null, worldpayAdditionalInfoDataMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFraudSightData_WhenUserIsNull_ShouldThrowException() {
        testObj.createFraudSightData(cartMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    public void createFraudSightData_ShouldPopulateTheFraudSightData() {
        doReturn(null).when(testObj).createCustomNumericFields();
        doReturn(null).when(testObj).createCustomStringFields();
        when(cartMock.getUser()).thenReturn(customerMock);
        when(cartMock.getPaymentAddress()).thenReturn(addressMock);
        when(customerMock.getName()).thenReturn(CUSTOMER_NAME);
        when(worldpayAdditionalInfoDataMock.getDateOfBirth()).thenReturn(BIRTHDAY_DATE);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartMock)).thenReturn(SHOPPER_ID);
        when(worldpayAddressConverterMock.convert(addressMock)).thenReturn(convertedAddressMock);

        final FraudSightData result = testObj.createFraudSightData(cartMock, worldpayAdditionalInfoDataMock);

        assertThat(result).isNotNull();
        assertThat(result.getShopperFields()).isNotNull();
        assertThat(result.getShopperFields().getShopperId()).isEqualTo(SHOPPER_ID);
        assertThat(result.getShopperFields().getShopperName()).isEqualTo(CUSTOMER_NAME);
        assertThat(result.getShopperFields().getBirthDate()).isNotNull();
        assertThat(result.getShopperFields().getBirthDate().getMonth()).isEqualTo("5");
        assertThat(result.getShopperFields().getBirthDate().getDayOfMonth()).isEqualTo("17");
        assertThat(result.getShopperFields().getShopperAddress()).isEqualTo(convertedAddressMock);
    }

    @Test(expected = NotImplementedException.class)
    public void createCustomStringFields_ShouldThrowException() {
        testObj.createCustomStringFields();
    }

    @Test(expected = NotImplementedException.class)
    public void createCustomNumericFields_ShouldThrowException() {
        testObj.createCustomNumericFields();
    }

    @Test
    public void addFraudSight_WhenFraudSightIsNull_ShouldDoNothing() {
        when(paymentReplyMock.getFraudSight()).thenReturn(null);

        testObj.addFraudSight(paymentTransactionModelMock, paymentReplyMock);

        verify(paymentTransactionModelMock, never()).setFraudSight(any());
    }

    @Test
    public void addFraudSight_WhenFraudSightIsPresent_ShouldSaveFraudSightOnTransaction() {
        when(paymentReplyMock.getFraudSight()).thenReturn(fraudSightMock);

        testObj.addFraudSight(paymentTransactionModelMock, paymentReplyMock);

        verify(paymentTransactionModelMock).setFraudSight(worldpayFrauSightMock);
        verify(modelServiceMock).save(paymentTransactionModelMock);
    }

    @Test
    public void populateRequestWithAdditionalData_WhenFraudSightIsEnabledAndDeviceIsNotNull_ShouldAddFraudSightAndDeviceData() {
        doReturn(fraudSightDataMock).when(testObj).createFraudSightData(cartMock, worldpayAdditionalInfoDataMock);
        doReturn(Boolean.TRUE).when(testObj).isFraudSightEnabled();
        when(worldpayAdditionalInfoDataMock.getDeviceSession()).thenReturn(DEVICE_SESSION);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock).withFraudSightData(fraudSightDataMock);
        verify(authoriseRequestParametersCreatorMock).withDeviceSession(DEVICE_SESSION);
    }

    @Test
    public void populateRequestWithAdditionalData_WhenFraudSightIsEnabledAndDeviceIsNull_ShouldAddFraudSight() {
        doReturn(fraudSightDataMock).when(testObj).createFraudSightData(cartMock, worldpayAdditionalInfoDataMock);
        when(worldpayAdditionalInfoDataMock.getDeviceSession()).thenReturn(null);
        doReturn(Boolean.TRUE).when(testObj).isFraudSightEnabled();

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock).withFraudSightData(fraudSightDataMock);
        verify(authoriseRequestParametersCreatorMock, never()).withDeviceSession(DEVICE_SESSION);
    }

    @Test
    public void populateRequestWithAdditionalData_WhenFraudSightIsDisabled_ShouldDoNothing() {
        doReturn(Boolean.FALSE).when(testObj).isFraudSightEnabled();

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verifyZeroInteractions(authoriseRequestParametersCreatorMock);
    }
}
