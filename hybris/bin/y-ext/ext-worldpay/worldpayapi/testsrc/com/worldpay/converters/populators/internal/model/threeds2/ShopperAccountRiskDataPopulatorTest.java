package com.worldpay.converters.populators.internal.model.threeds2;

import com.worldpay.data.Date;
import com.worldpay.data.threeds2.ShopperAccountRiskData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ShopperAccountRiskDataPopulatorTest {

    private static final String TRANSACTIONS_ATTEMPTED_LAST_DAY = "transactionsAttemptedLastDay";
    private static final String TRANSACTIONS_ATTEMPTED_LAST_YEAR = "transactionsAttemptedLastYear";
    private static final String PREVIOUS_SUSPICIOUS_ACTIVITY = "previousSuspiciousActivity";
    private static final String ADD_CARD_ATTEMPTS_LAST_DAY = "addCardAttemptsLastDay";
    private static final String SHIPPING_NAME_MATCHES_ACCOUNT_NAME = "shippingNameMatchesAccountName";
    private static final String SHOPPER_ACCOUNT_AGE_INDICATOR = "shopperAccountAgeIndicator";
    private static final String SHOPPER_ACCOUNT_CHANGE_INDICATOR = "shopperAccountChangeIndicator";
    private static final String SHOPPER_ACCOUNT_PASSWORD_CHANGE_INDICATOR = "shopperAccountPasswordChangeIndicator";
    private static final String SHOPPER_ACCOUNT_SHIPPING_ADDRESS_USAGE_INDICATOR = "shopperAccountShippingAddressUsageIndicator";
    private static final String SHOPPER_ACCOUNT_PAYMENT_ACCOUNT_INDICATOR = "shopperAccountPaymentAccountIndicator";

    @InjectMocks
    private ShopperAccountRiskDataPopulator testObj;

    @Mock
    private Converter<Date, com.worldpay.internal.model.Date> internalDateConverterMock;

    @Mock
    private ShopperAccountRiskData sourceMock;
    @Mock
    private Date date1Mock, date2Mock, date3Mock, date4Mock, date5Mock;
    @Mock
    private com.worldpay.internal.model.Date internalDate1Mock, internalDate2Mock, internalDate3Mock, internalDate4Mock, internalDate5Mock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.ShopperAccountRiskData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_ShouldPopulateShopperAccountRiskData() {
        when(sourceMock.getTransactionsAttemptedLastDay()).thenReturn(TRANSACTIONS_ATTEMPTED_LAST_DAY);
        when(sourceMock.getTransactionsAttemptedLastYear()).thenReturn(TRANSACTIONS_ATTEMPTED_LAST_YEAR);
        when(sourceMock.getPreviousSuspiciousActivity()).thenReturn(PREVIOUS_SUSPICIOUS_ACTIVITY);
        when(sourceMock.getAddCardAttemptsLastDay()).thenReturn(ADD_CARD_ATTEMPTS_LAST_DAY);
        when(sourceMock.getShippingNameMatchesAccountName()).thenReturn(SHIPPING_NAME_MATCHES_ACCOUNT_NAME);
        when(sourceMock.getShopperAccountAgeIndicator()).thenReturn(SHOPPER_ACCOUNT_AGE_INDICATOR);
        when(sourceMock.getShopperAccountChangeIndicator()).thenReturn(SHOPPER_ACCOUNT_CHANGE_INDICATOR);
        when(sourceMock.getShopperAccountPasswordChangeIndicator()).thenReturn(SHOPPER_ACCOUNT_PASSWORD_CHANGE_INDICATOR);
        when(sourceMock.getShopperAccountShippingAddressUsageIndicator()).thenReturn(SHOPPER_ACCOUNT_SHIPPING_ADDRESS_USAGE_INDICATOR);
        when(sourceMock.getShopperAccountPaymentAccountIndicator()).thenReturn(SHOPPER_ACCOUNT_PAYMENT_ACCOUNT_INDICATOR);

        when(sourceMock.getShopperAccountCreationDate()).thenReturn(date1Mock);
        when(sourceMock.getShopperAccountShippingAddressFirstUseDate()).thenReturn(date2Mock);
        when(sourceMock.getShopperAccountModificationDate()).thenReturn(date3Mock);
        when(sourceMock.getShopperAccountPaymentAccountFirstUseDate()).thenReturn(date4Mock);
        when(sourceMock.getShopperAccountPasswordChangeDate()).thenReturn(date5Mock);

        when(internalDateConverterMock.convert(date1Mock)).thenReturn(internalDate1Mock);
        when(internalDateConverterMock.convert(date2Mock)).thenReturn(internalDate2Mock);
        when(internalDateConverterMock.convert(date3Mock)).thenReturn(internalDate3Mock);
        when(internalDateConverterMock.convert(date4Mock)).thenReturn(internalDate4Mock);
        when(internalDateConverterMock.convert(date5Mock)).thenReturn(internalDate5Mock);

        final com.worldpay.internal.model.ShopperAccountRiskData targetMock = new com.worldpay.internal.model.ShopperAccountRiskData();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getTransactionsAttemptedLastDay()).isEqualTo(TRANSACTIONS_ATTEMPTED_LAST_DAY);
        assertThat(targetMock.getTransactionsAttemptedLastYear()).isEqualTo(TRANSACTIONS_ATTEMPTED_LAST_YEAR);
        assertThat(targetMock.getPreviousSuspiciousActivity()).isEqualTo(PREVIOUS_SUSPICIOUS_ACTIVITY);
        assertThat(targetMock.getAddCardAttemptsLastDay()).isEqualTo(ADD_CARD_ATTEMPTS_LAST_DAY);
        assertThat(targetMock.getShippingNameMatchesAccountName()).isEqualTo(SHIPPING_NAME_MATCHES_ACCOUNT_NAME);
        assertThat(targetMock.getShopperAccountAgeIndicator()).isEqualTo(SHOPPER_ACCOUNT_AGE_INDICATOR);
        assertThat(targetMock.getShopperAccountChangeIndicator()).isEqualTo(SHOPPER_ACCOUNT_CHANGE_INDICATOR);
        assertThat(targetMock.getShopperAccountPasswordChangeIndicator()).isEqualTo(SHOPPER_ACCOUNT_PASSWORD_CHANGE_INDICATOR);
        assertThat(targetMock.getShopperAccountShippingAddressUsageIndicator()).isEqualTo(SHOPPER_ACCOUNT_SHIPPING_ADDRESS_USAGE_INDICATOR);
        assertThat(targetMock.getShopperAccountPaymentAccountIndicator()).isEqualTo(SHOPPER_ACCOUNT_PAYMENT_ACCOUNT_INDICATOR);

        assertThat(targetMock.getShopperAccountCreationDate().getDate()).isEqualTo(internalDate1Mock);
        assertThat(targetMock.getShopperAccountShippingAddressFirstUseDate().getDate()).isEqualTo(internalDate2Mock);
        assertThat(targetMock.getShopperAccountModificationDate().getDate()).isEqualTo(internalDate3Mock);
        assertThat(targetMock.getShopperAccountPaymentAccountFirstUseDate().getDate()).isEqualTo(internalDate4Mock);
        assertThat(targetMock.getShopperAccountPasswordChangeDate().getDate()).isEqualTo(internalDate5Mock);
    }

    @Test
    public void populate_WhenDateIsNull_ShouldNotPopulateInternalDate() {
        when(sourceMock.getShopperAccountCreationDate()).thenReturn(null);
        when(internalDateConverterMock.convert(date1Mock)).thenReturn(internalDate1Mock);

        final com.worldpay.internal.model.ShopperAccountRiskData targetMock = new com.worldpay.internal.model.ShopperAccountRiskData();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getShopperAccountCreationDate()).isNull();
    }
}
