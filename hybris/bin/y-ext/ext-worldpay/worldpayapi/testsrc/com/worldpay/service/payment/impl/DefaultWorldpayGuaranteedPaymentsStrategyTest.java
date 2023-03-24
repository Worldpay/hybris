package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.data.Date;
import com.worldpay.data.GuaranteedPaymentsData;
import com.worldpay.data.Product;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.request.AuthoriseRequestParameters;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.DiscountValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayGuaranteedPaymentsStrategyTest {

    private static final String CUSTOMER_NAME = "customerName";
    private static final String PHONE = "phone";
    private static final String SHOPPER_ID = "shopperId";
    private static final String GUID = "guid";
    private static final double DELIVERY_COST = 10.00;
    private static final String DEVICE_SESSION = "deviceSession";
    private static final String NULL = "null";
    private static final String DISCOUNT_CODE_1 = "discountCode1";
    private static final String DISCOUNT_CODE_2 = "discountCode2";
    private static final String DISCOUNT_CODE_ACTION_2 = "Action[discountCode2]";
    private static final double DISCOUNT_VALUE_1 = 10.0;
    private static final double DISCOUNT_VALUE_2 = 15.0;
    public static final String CUSTOMER_ID = "customerId";
    public static final String CART_CODE = "cartCode";

    @Spy
    @InjectMocks
    private DefaultWorldpayGuaranteedPaymentsStrategy testObj;

    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private BaseSiteService baseSiteServiceMock;
    @Mock
    private Converter<java.util.Date, Date> worldpayDateConverterMock;
    @Mock
    private Converter<AbstractOrderEntryModel, Product> worldpayProductConverterMock;

    @Mock
    private CartModel cartMock;
    @Mock
    private CMSSiteModel siteMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private CustomerModel customerMock;
    @Mock
    private AddressModel addressMock;
    @Mock
    private com.worldpay.data.Date date;
    @Mock
    private GuaranteedPaymentsData guaranteedPaymentsDataMock;
    @Mock
    private AuthoriseRequestParameters.AuthoriseRequestParametersCreator authoriseRequestParametersCreatorMock;
    @Mock
    private DiscountValue discountValue1Mock;
    @Mock
    private DiscountValue discountValue2Mock;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(testObj, "worldpayProductConverter", worldpayProductConverterMock);
        ReflectionTestUtils.setField(testObj, "worldpayDateConverter", worldpayDateConverterMock);
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(siteMock);

        when(cartMock.getUser()).thenReturn(customerMock);
        when(customerMock.getName()).thenReturn(CUSTOMER_NAME);
        when(customerMock.getUid()).thenReturn(GUID);
        when(customerMock.getAddresses()).thenReturn(Collections.singletonList(addressMock));
        when(customerMock.getCreationtime()).thenReturn(new java.util.Date());
        when(cartMock.getDeliveryCost()).thenReturn(DELIVERY_COST);
        when(cartMock.getCode()).thenReturn(CART_CODE);
        when(addressMock.getPhone1()).thenReturn(PHONE);
        when(worldpayCartServiceMock.getAuthenticatedShopperId(cartMock)).thenReturn(SHOPPER_ID);
        when(worldpayDateConverterMock.convert(customerMock.getCreationtime())).thenReturn(date);
        when(customerMock.getCustomerID()).thenReturn(CUSTOMER_ID);
    }

    @Test
    public void isGuaranteedPaymentsEnabled_WhenGPIsEnabledForSite_ShouldReturnTrue() {
        when(siteMock.getEnableGP()).thenReturn(true);

        final boolean result = testObj.isGuaranteedPaymentsEnabled();

        assertThat(result).isTrue();
    }

    @Test
    public void isGuaranteedPaymentsEnabled_WhenGPIsDisabledForSite_ShouldReturnFalse() {
        when(siteMock.getEnableGP()).thenReturn(false);

        final boolean result = testObj.isGuaranteedPaymentsEnabled();

        assertThat(result).isFalse();
    }

    @Test
    public void isGuaranteedPaymentsEnabled_WhenGPIsEnabledForGivenSite_ShouldReturnTrue() {
        when(siteMock.getEnableGP()).thenReturn(true);

        final boolean result = testObj.isGuaranteedPaymentsEnabled(siteMock);

        assertThat(result).isTrue();
    }

    @Test
    public void isGuaranteedPaymentsEnabled_WhenGPIsDisabledForGivenSite_ShouldReturnFalse() {
        when(siteMock.getEnableGP()).thenReturn(false);

        final boolean result = testObj.isGuaranteedPaymentsEnabled(siteMock);

        assertThat(result).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createGuaranteedPaymentsData_WhenCartIsNull_ShouldThrowException() {
        testObj.createGuaranteedPaymentsData(null, worldpayAdditionalInfoDataMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createGuaranteedPaymentsData_WhenUserIsNull_ShouldThrowException() {
        when(cartMock.getUser()).thenReturn(null);

        testObj.createGuaranteedPaymentsData(cartMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    public void createGuaranteedPaymentsData_ShouldPopulateTheGuaranteedPaymentsData() {
        final GuaranteedPaymentsData result = testObj.createGuaranteedPaymentsData(cartMock, worldpayAdditionalInfoDataMock);

        assertThat(result).isNotNull();
        assertThat(result.getTotalShippingCost()).isEqualTo(String.valueOf(DELIVERY_COST));
        assertThat(result.getUserAccount()).isNotNull();
        assertThat(result.getUserAccount().getUserAccountCreatedDate()).isEqualTo(date);
        assertThat(result.getUserAccount().getUserAccountEmailAddress()).isEqualTo(GUID);
        assertThat(result.getUserAccount().getUserAccountNumber()).isEqualTo(SHOPPER_ID);
        assertThat(result.getUserAccount().getUserAccountUserName()).isEqualTo(CUSTOMER_NAME);
        assertThat(result.getUserAccount().getUserAccountPhoneNumber()).isEqualTo(PHONE);
        assertThat(result.getMemberships()).isEmpty();

    }

    @Test
    public void populateRequestWithAdditionalData_WhenGuaranteedPaymentsIsEnabledAndDeviceIsNotNull_ShouldAddGuaranteedPaymentsAndDeviceData() {
        doReturn(guaranteedPaymentsDataMock).when(testObj).createGuaranteedPaymentsData(cartMock, worldpayAdditionalInfoDataMock);
        doReturn(Boolean.TRUE).when(testObj).isGuaranteedPaymentsEnabled();
        when(worldpayAdditionalInfoDataMock.getDeviceSession()).thenReturn(DEVICE_SESSION);

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock).withGuaranteedPaymentsData(guaranteedPaymentsDataMock);
        verify(authoriseRequestParametersCreatorMock).withCheckoutId(CUSTOMER_ID + "_" + CART_CODE);
        verify(authoriseRequestParametersCreatorMock).withDeviceSession(DEVICE_SESSION);
    }

    @Test
    public void populateRequestWithAdditionalData_WhenGuaranteedPaymentsIsEnabledAndDeviceIsNull_ShouldAddGuaranteedPayments() {
        doReturn(guaranteedPaymentsDataMock).when(testObj).createGuaranteedPaymentsData(cartMock, worldpayAdditionalInfoDataMock);
        when(worldpayAdditionalInfoDataMock.getDeviceSession()).thenReturn(null);
        doReturn(Boolean.TRUE).when(testObj).isGuaranteedPaymentsEnabled();

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verify(authoriseRequestParametersCreatorMock).withGuaranteedPaymentsData(guaranteedPaymentsDataMock);
        verify(authoriseRequestParametersCreatorMock).withCheckoutId(CUSTOMER_ID + "_" + CART_CODE);
        verify(authoriseRequestParametersCreatorMock, never()).withDeviceSession(DEVICE_SESSION);
    }

    @Test
    public void populateRequestWithAdditionalData_WhenGuaranteedPaymentsIsDisabled_ShouldDoNothing() {
        doReturn(Boolean.FALSE).when(testObj).isGuaranteedPaymentsEnabled();

        testObj.populateRequestWithAdditionalData(cartMock, worldpayAdditionalInfoDataMock, authoriseRequestParametersCreatorMock);

        verifyZeroInteractions(authoriseRequestParametersCreatorMock);
    }

    @Test
    public void createDiscountCodes_ShouldPopulateTheDiscountCodes_WhenHasDiscounts() {
        when(cartMock.getGlobalDiscountValues()).thenReturn(List.of(discountValue1Mock, discountValue2Mock));
        when(discountValue1Mock.getCode()).thenReturn(DISCOUNT_CODE_1);
        when(discountValue1Mock.getAppliedValue()).thenReturn(DISCOUNT_VALUE_1);
        when(discountValue1Mock.isAbsolute()).thenReturn(Boolean.TRUE);
        when(discountValue2Mock.getCode()).thenReturn(DISCOUNT_CODE_ACTION_2);
        when(discountValue2Mock.getAppliedValue()).thenReturn(DISCOUNT_VALUE_2);
        when(discountValue2Mock.isAbsolute()).thenReturn(Boolean.FALSE);


        final GuaranteedPaymentsData result = testObj.createGuaranteedPaymentsData(cartMock, worldpayAdditionalInfoDataMock);

        assertThat(result.getDiscountCodes()).hasSize(2);
        assertThat(result.getDiscountCodes().get(0).getPurchaseDiscountCode()).isEqualTo(DISCOUNT_CODE_1);
        assertThat(result.getDiscountCodes().get(0).getPurchaseDiscountAmount()).isEqualTo(String.valueOf(DISCOUNT_VALUE_1));
        assertThat(result.getDiscountCodes().get(1).getPurchaseDiscountCode()).isEqualTo(DISCOUNT_CODE_2);
        assertThat(result.getDiscountCodes().get(1).getPurchaseDiscountPercentage()).isEqualTo(String.valueOf(DISCOUNT_VALUE_2));
    }

    @Test
    public void createDiscountCodes_ShouldPopulateEmptyObject_WhenHasNoDiscounts() {
        when(cartMock.getGlobalDiscountValues()).thenReturn(Collections.emptyList());

        final GuaranteedPaymentsData result = testObj.createGuaranteedPaymentsData(cartMock, worldpayAdditionalInfoDataMock);

        assertThat(result.getDiscountCodes()).hasSize(1);
        assertThat(result.getDiscountCodes().get(0).getPurchaseDiscountCode()).isEqualTo(NULL);
    }

}
