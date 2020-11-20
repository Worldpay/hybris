package com.worldpay.facades.impl;

import com.worldpay.customer.WorldpayCustomerAccountService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorldpayUserFacadeTest {

    private static final String ADDRESS_CODE = "addressCode";
    private static final String USER_ID = "userId";
    private static final String USER_UID = "userUid";
    private static final String PAYMENT_INFO_CODE = "paymentInfoCode";
    private static final long PAYMENT_INFO_PK_LONG_VALUE = 123456L;
    private static final long SAVED_APM_PAYMENT_INFO_PK_LONG_VALUE = 9876L;
    private static final long CC_PAYMENT_INFO_PK_LONG_VALUE = 123212L;
    private static final long UNSAVED_APM_PAYMENT_INFO_PK_LONG_VALUE = 24234234234L;

    @InjectMocks
    private DefaultWorldpayUserFacade testObj;

    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private UserFacade userFacadeMock;
    @Mock(name = "apmPaymentInfoConverter")
    private Converter<WorldpayAPMPaymentInfoModel, CCPaymentInfoData> apmPaymentInfoConverterMock;
    @Mock(name = "creditCardPaymentInfoConverter")
    private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverterMock;
    @Mock
    private WorldpayCustomerAccountService customerAccountServiceMock;

    @Mock
    private TitleData titleDataMock;
    @Mock
    private AddressData addressDataMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private WorldpayAPMPaymentInfoModel savedApmPaymentInfoModelMock, unsavedApmPaymentInfoModelMock;
    @Mock
    private CCPaymentInfoData ccPaymentInfoDataMock, savedApmPaymentInfoDataMock, unsavedAPMPaymentInfoDataMock;
    @Mock
    private CreditCardPaymentInfoModel ccPaymentInfoModelMock;

    final private PK paymentInfoPk = PK.fromLong(PAYMENT_INFO_PK_LONG_VALUE);
    final private PK savedApmPaymentInfoPk = PK.fromLong(SAVED_APM_PAYMENT_INFO_PK_LONG_VALUE);
    final private PK unsavedApmPaymentInfoPk = PK.fromLong(UNSAVED_APM_PAYMENT_INFO_PK_LONG_VALUE);
    final private PK ccPaymentInfoPk = PK.fromLong(CC_PAYMENT_INFO_PK_LONG_VALUE);

    @Before
    public void setUp() {
        testObj = new DefaultWorldpayUserFacade(checkoutCustomerStrategyMock, userFacadeMock, apmPaymentInfoConverterMock,
            creditCardPaymentInfoConverterMock, customerAccountServiceMock);

        when(checkoutCustomerStrategyMock.getCurrentUserForCheckout()).thenReturn(customerModelMock);
        when(paymentInfoModelMock.getPk()).thenReturn(paymentInfoPk);
        when(ccPaymentInfoModelMock.getPk()).thenReturn(ccPaymentInfoPk);
        when(savedApmPaymentInfoModelMock.getPk()).thenReturn(savedApmPaymentInfoPk);
        when(unsavedApmPaymentInfoModelMock.getPk()).thenReturn(unsavedApmPaymentInfoPk);
        when(paymentInfoModelMock.getDuplicate()).thenReturn(Boolean.FALSE);
        when(ccPaymentInfoModelMock.getDuplicate()).thenReturn(Boolean.FALSE);
        when(savedApmPaymentInfoModelMock.getDuplicate()).thenReturn(Boolean.FALSE);
        when(unsavedApmPaymentInfoModelMock.getDuplicate()).thenReturn(Boolean.FALSE);
        when(paymentInfoModelMock.isSaved()).thenReturn(Boolean.TRUE);
        when(ccPaymentInfoModelMock.isSaved()).thenReturn(Boolean.TRUE);
        when(savedApmPaymentInfoModelMock.isSaved()).thenReturn(Boolean.TRUE);
        when(unsavedApmPaymentInfoModelMock.isSaved()).thenReturn(Boolean.FALSE);
        when(customerModelMock.getPaymentInfos()).thenReturn(
            List.of(paymentInfoModelMock, ccPaymentInfoModelMock, savedApmPaymentInfoModelMock, unsavedApmPaymentInfoModelMock));

    }

    @Test
    public void getTitles_ShouldReturnAllTitles() {
        when(userFacadeMock.getTitles()).thenReturn(List.of(titleDataMock));

        final List<TitleData> result = testObj.getTitles();

        assertThat(result).containsExactly(titleDataMock);
    }

    @Test
    public void isAddressBookEmpty_WhenIsEmpty_ShouldReturnTrue() {
        when(userFacadeMock.isAddressBookEmpty()).thenReturn(Boolean.TRUE);

        final boolean result = testObj.isAddressBookEmpty();

        assertThat(result).isTrue();
    }

    @Test
    public void isAddressBookEmpty_WhenIsNotEmpty_ShouldReturnFalse() {
        when(userFacadeMock.isAddressBookEmpty()).thenReturn(Boolean.FALSE);

        final boolean result = testObj.isAddressBookEmpty();

        assertThat(result).isFalse();
    }

    @Test
    public void getAddressBook_ShouldReturnAllAddresses() {
        when(userFacadeMock.getAddressBook()).thenReturn(List.of(addressDataMock));

        final List<AddressData> result = testObj.getAddressBook();

        assertThat(result).containsExactly(addressDataMock);
    }

    @Test
    public void addAddress_ShouldAddTheGivenAddress() {
        testObj.addAddress(addressDataMock);

        verify(userFacadeMock).addAddress(addressDataMock);
    }

    @Test
    public void removeAddress_ShouldRemoveTheGivenAddress() {
        testObj.removeAddress(addressDataMock);

        verify(userFacadeMock).removeAddress(addressDataMock);
    }

    @Test
    public void editAddress_ShouldEditTheGivenAddress() {
        testObj.editAddress(addressDataMock);

        verify(userFacadeMock).editAddress(addressDataMock);
    }

    @Test
    public void getDefaultAddress_ShouldGetTheDefaultAddress() {
        when(userFacadeMock.getDefaultAddress()).thenReturn(addressDataMock);

        final AddressData result = testObj.getDefaultAddress();

        assertThat(result).isEqualTo(addressDataMock);
    }

    @Test
    public void setDefaultAddress_ShouldSetTheGivenAddressAsDefault() {
        testObj.setDefaultAddress(addressDataMock);

        verify(userFacadeMock).setDefaultAddress(addressDataMock);
    }

    @Test
    public void getAddressForCode_WhenNoAddressExists_ShouldReturnNull() {
        when(userFacadeMock.getAddressForCode(ADDRESS_CODE)).thenReturn(null);

        final AddressData result = testObj.getAddressForCode(ADDRESS_CODE);

        assertThat(result).isNull();
    }

    @Test
    public void getAddressForCode_WhenAddressExists_ShouldReturnTheAddress() {
        when(userFacadeMock.getAddressForCode(ADDRESS_CODE)).thenReturn(addressDataMock);

        final AddressData result = testObj.getAddressForCode(ADDRESS_CODE);

        assertThat(result).isEqualTo(addressDataMock);
    }

    @Test
    public void isDefaultAddress_WhenIsDefaultAddress_ShouldReturnTrue() {
        when(userFacadeMock.isDefaultAddress(ADDRESS_CODE)).thenReturn(Boolean.TRUE);

        final boolean result = testObj.isDefaultAddress(ADDRESS_CODE);

        assertThat(result).isTrue();
    }

    @Test
    public void isDefaultAddress_WhenIsNotDefaultAddress_ShouldReturnFalse() {
        when(userFacadeMock.isDefaultAddress(ADDRESS_CODE)).thenReturn(Boolean.FALSE);

        final boolean result = testObj.isDefaultAddress(ADDRESS_CODE);

        assertThat(result).isFalse();
    }

    @Test
    public void getCCPaymentInfos_WhenSavedIsTrue_ShouldReturnAllCreditCardAndAPMSavedPaymentInfoData() {
        when(apmPaymentInfoConverterMock.convert(savedApmPaymentInfoModelMock)).thenReturn(savedApmPaymentInfoDataMock);
        when(creditCardPaymentInfoConverterMock.convert(ccPaymentInfoModelMock)).thenReturn(ccPaymentInfoDataMock);

        final List<CCPaymentInfoData> result = testObj.getCCPaymentInfos(Boolean.TRUE);

        assertThat(result).containsExactlyInAnyOrder(ccPaymentInfoDataMock, savedApmPaymentInfoDataMock);
    }


    @Test
    public void getCCPaymentInfos_WhenSavedIsFalse_ShouldReturnAllCreditCardAndAPMPaymentInfoData() {
        when(apmPaymentInfoConverterMock.convert(savedApmPaymentInfoModelMock)).thenReturn(savedApmPaymentInfoDataMock);
        when(apmPaymentInfoConverterMock.convert(unsavedApmPaymentInfoModelMock)).thenReturn(unsavedAPMPaymentInfoDataMock);
        when(creditCardPaymentInfoConverterMock.convert(ccPaymentInfoModelMock)).thenReturn(ccPaymentInfoDataMock);

        final List<CCPaymentInfoData> result = testObj.getCCPaymentInfos(Boolean.FALSE);

        assertThat(result).containsExactlyInAnyOrder(ccPaymentInfoDataMock, savedApmPaymentInfoDataMock, unsavedAPMPaymentInfoDataMock);
    }

    @Test
    public void getCCPaymentInfos_WhenPaymentMethodIsDefault_ShouldSetItAsDefaultAndReturnItAtFirstPosition() {
        when(customerModelMock.getPaymentInfos()).thenReturn(List.of(savedApmPaymentInfoModelMock, unsavedApmPaymentInfoModelMock));
        when(apmPaymentInfoConverterMock.convert(savedApmPaymentInfoModelMock)).thenReturn(savedApmPaymentInfoDataMock);
        when(apmPaymentInfoConverterMock.convert(unsavedApmPaymentInfoModelMock)).thenReturn(unsavedAPMPaymentInfoDataMock);
        when(customerModelMock.getDefaultPaymentInfo()).thenReturn(unsavedApmPaymentInfoModelMock);

        final List<CCPaymentInfoData> result = testObj.getCCPaymentInfos(Boolean.FALSE);

        verify(unsavedAPMPaymentInfoDataMock).setDefaultPaymentInfo(true);

        assertThat(result).containsExactly(unsavedAPMPaymentInfoDataMock, savedApmPaymentInfoDataMock);
    }

    @Test
    public void getCCPaymentInfoForCode_WhenIsCCPaymentInfo_ShouldUseCCPaymentConverter() {
        when(creditCardPaymentInfoConverterMock.convert(ccPaymentInfoModelMock)).thenReturn(ccPaymentInfoDataMock);

        final CCPaymentInfoData result = testObj.getCCPaymentInfoForCode(String.valueOf(CC_PAYMENT_INFO_PK_LONG_VALUE));

        assertThat(result).isEqualTo(ccPaymentInfoDataMock);

        verify(creditCardPaymentInfoConverterMock).convert(ccPaymentInfoModelMock);
    }

    @Test
    public void getCCPaymentInfoForCode_WhenIsAPMPaymentInfo_ShouldUseAPMPaymentConverter() {
        when(apmPaymentInfoConverterMock.convert(savedApmPaymentInfoModelMock)).thenReturn(ccPaymentInfoDataMock);

        final CCPaymentInfoData result = testObj.getCCPaymentInfoForCode(String.valueOf(SAVED_APM_PAYMENT_INFO_PK_LONG_VALUE));

        assertThat(result).isEqualTo(ccPaymentInfoDataMock);

        verify(apmPaymentInfoConverterMock).convert(savedApmPaymentInfoModelMock);
    }

    @Test
    public void getCCPaymentInfoForCode_WhenPaymentInfoIsDuplicated_ShouldReturnNull() {
        when((savedApmPaymentInfoModelMock.getDuplicate())).thenReturn(Boolean.TRUE);

        final CCPaymentInfoData result = testObj.getCCPaymentInfoForCode(String.valueOf(SAVED_APM_PAYMENT_INFO_PK_LONG_VALUE));

        assertThat(result).isNull();
    }

    @Test
    public void getCCPaymentInfoForCode_WhenPaymentInfoIsNotCreditCardOrAPM_ShouldReturnNull() {
        final CCPaymentInfoData result = testObj.getCCPaymentInfoForCode(String.valueOf(PAYMENT_INFO_PK_LONG_VALUE));

        assertThat(result).isNull();
    }

    @Test
    public void updateCCPaymentInfo() {
        testObj.updateCCPaymentInfo(savedApmPaymentInfoDataMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeCCPaymentInfo_WhenIdIsNull_ShouldThrowIllegalArgumentException() {
        testObj.removeCCPaymentInfo(null);
    }

    @Test
    public void removeCCPaymentInfo_WhenPaymentInfoIsCreditCardType_ShouldRemovePaymentInfo() {
        when(customerModelMock.getPaymentInfos()).thenReturn(List.of(ccPaymentInfoModelMock, savedApmPaymentInfoModelMock));
        when(savedApmPaymentInfoModelMock.isSaved()).thenReturn(Boolean.FALSE);

        testObj.removeCCPaymentInfo(String.valueOf(SAVED_APM_PAYMENT_INFO_PK_LONG_VALUE));

        verify(customerAccountServiceMock).deleteAPMPaymentInfo(customerModelMock, savedApmPaymentInfoModelMock);
        verify(customerAccountServiceMock).setDefaultPaymentInfo(customerModelMock, ccPaymentInfoModelMock);
    }

    @Test
    public void removeCCPaymentInfo_WhenPaymentInfoIsAPMType_ShouldRemovePaymentInfo() {
        when(customerModelMock.getPaymentInfos()).thenReturn(List.of(ccPaymentInfoModelMock, savedApmPaymentInfoModelMock));
        when(ccPaymentInfoModelMock.isSaved()).thenReturn(Boolean.FALSE);

        testObj.removeCCPaymentInfo(String.valueOf(CC_PAYMENT_INFO_PK_LONG_VALUE));

        verify(customerAccountServiceMock).deleteCCPaymentInfo(customerModelMock, ccPaymentInfoModelMock);
        verify(customerAccountServiceMock).setDefaultPaymentInfo(customerModelMock, savedApmPaymentInfoModelMock);
    }

    @Test
    public void removeCCPaymentInfo_WhenPaymentInfoDoesNotBelongToTheCustomer_ShouldDoNothing() {
        when(customerModelMock.getPaymentInfos()).thenReturn(Collections.emptyList());

        testObj.removeCCPaymentInfo(PAYMENT_INFO_CODE);

        verifyZeroInteractions(customerAccountServiceMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void runlinkCCPaymentInfo_WhenIdIsNull_ShouldThrowIllegalArgumentException() {
        testObj.unlinkCCPaymentInfo(null);
    }

    @Test
    public void unlinkCCPaymentInfo_WhenPaymentInfoIsCreditCardType_ShouldRemovePaymentInfo() {
        when(customerModelMock.getPaymentInfos()).thenReturn(List.of(ccPaymentInfoModelMock, savedApmPaymentInfoModelMock));
        when(savedApmPaymentInfoModelMock.isSaved()).thenReturn(Boolean.FALSE);

        testObj.unlinkCCPaymentInfo(String.valueOf(SAVED_APM_PAYMENT_INFO_PK_LONG_VALUE));

        verify(customerAccountServiceMock).deleteAPMPaymentInfo(customerModelMock, savedApmPaymentInfoModelMock);
        verify(customerAccountServiceMock).setDefaultPaymentInfo(customerModelMock, ccPaymentInfoModelMock);
    }

    @Test
    public void unlinkCCPaymentInfo_WhenPaymentInfoIsAPMType_ShouldRemovePaymentInfo() {
        when(customerModelMock.getPaymentInfos()).thenReturn(List.of(ccPaymentInfoModelMock, savedApmPaymentInfoModelMock));
        when(ccPaymentInfoModelMock.isSaved()).thenReturn(Boolean.FALSE);


        testObj.unlinkCCPaymentInfo(String.valueOf(CC_PAYMENT_INFO_PK_LONG_VALUE));

        verify(customerAccountServiceMock).deleteCCPaymentInfo(customerModelMock, ccPaymentInfoModelMock);
        verify(customerAccountServiceMock).setDefaultPaymentInfo(customerModelMock, savedApmPaymentInfoModelMock);
    }

    @Test
    public void unlinkCCPaymentInfo_WhenPaymentInfoDoesNotBelongToTheCustomer_ShouldDoNothing() {
        when(customerModelMock.getPaymentInfos()).thenReturn(Collections.emptyList());

        testObj.unlinkCCPaymentInfo(PAYMENT_INFO_CODE);

        verifyZeroInteractions(customerAccountServiceMock);
    }

    @Test
    public void unlinkCCPaymentInfo() {
        testObj.unlinkCCPaymentInfo(PAYMENT_INFO_CODE);
    }

    @Test
    public void setDefaultPaymentInfo_WhenPaymentInfoBelongsToCustoemr_ShouldSetPaymentInfoAsDefaultForCurrentCustomer() {
        when(savedApmPaymentInfoDataMock.getId()).thenReturn(String.valueOf(SAVED_APM_PAYMENT_INFO_PK_LONG_VALUE));

        testObj.setDefaultPaymentInfo(savedApmPaymentInfoDataMock);

        verify(customerAccountServiceMock).setDefaultPaymentInfo(customerModelMock, savedApmPaymentInfoModelMock);
    }

    @Test
    public void setDefaultPaymentInfo_WhenPaymentInfoDoesNotBelongsToCustomer_ShouldDoNothing() {
        when(customerModelMock.getPaymentInfos()).thenReturn(Collections.emptyList());
        when(savedApmPaymentInfoDataMock.getId()).thenReturn(String.valueOf(SAVED_APM_PAYMENT_INFO_PK_LONG_VALUE));

        testObj.setDefaultPaymentInfo(savedApmPaymentInfoDataMock);

        verifyZeroInteractions(customerAccountServiceMock);
    }

    @Test
    public void syncSessionLanguage() {
        testObj.syncSessionLanguage();

        verify(userFacadeMock).syncSessionLanguage();
    }

    @Test
    public void syncSessionCurrency() {
        testObj.syncSessionCurrency();

        verify(userFacadeMock).syncSessionCurrency();
    }

    @Test
    public void isAnonymousUser_ShouldReturnIfUserIsAnonymous() {
        when(userFacadeMock.isAnonymousUser()).thenReturn(Boolean.TRUE);

        final boolean result = testObj.isAnonymousUser();

        assertThat(result).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void isUserExisting_ShouldReturnIfUserExists() {
        when(userFacadeMock.isUserExisting(USER_ID)).thenReturn(Boolean.TRUE);

        final boolean result = testObj.isUserExisting(USER_ID);

        assertThat(result).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void getUserUID_ShouldGetTheUserID() {
        when(userFacadeMock.getUserUID(USER_ID)).thenReturn(USER_UID);

        final String result = testObj.getUserUID(USER_ID);

        assertThat(result).isEqualTo(USER_UID);
    }

    @Test
    public void setCurrentUser_ShouldSetTheCurrentUser() {
        testObj.setCurrentUser(USER_ID);

        verify(userFacadeMock).setCurrentUser(USER_ID);
    }
}
