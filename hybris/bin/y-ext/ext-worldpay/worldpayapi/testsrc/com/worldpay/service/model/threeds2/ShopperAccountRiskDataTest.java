package com.worldpay.service.model.threeds2;

import com.worldpay.service.model.Date;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ShopperAccountRiskDataTest {

    @InjectMocks
    private ShopperAccountRiskData testObj;

    @Mock(name = "shopperAccountCreationDate")
    private Date shopperAccountCreationDateMock;
    @Mock(name = "shopperAccountShippingAddressFirstUseDate")
    private Date shopperAccountShippingAddressFirstUseDateMock;
    @Mock(name = "shopperAccountModificationDate")
    private Date shopperAccountModificationDateMock;
    @Mock(name = "shopperAccountPaymentAccountFirstUseDate")
    private Date shopperAccountPaymentAccountFirstUseDateMock;
    @Mock(name = "shopperAccountPasswordChangeDate")
    private Date shopperAccountPasswordChangeDateMock;

    @Mock
    private com.worldpay.internal.model.Date intShopperAccountCreationDateMock;
    @Mock
    private com.worldpay.internal.model.Date intShopperAccountShippingAddressFirstUseDateMock;
    @Mock
    private com.worldpay.internal.model.Date intShopperAccountModificationDateMock;
    @Mock
    private com.worldpay.internal.model.Date intShopperAccountPaymentAccountFirstUseDateMock;
    @Mock
    private com.worldpay.internal.model.Date intShopperAccountPasswordChangeDateMock;

    @Before
    public void setUp() throws Exception {
        when(shopperAccountCreationDateMock.transformToInternalModel()).thenReturn(intShopperAccountCreationDateMock);
        when(shopperAccountShippingAddressFirstUseDateMock.transformToInternalModel()).thenReturn(intShopperAccountShippingAddressFirstUseDateMock);
        when(shopperAccountModificationDateMock.transformToInternalModel()).thenReturn(intShopperAccountModificationDateMock);
        when(shopperAccountPaymentAccountFirstUseDateMock.transformToInternalModel()).thenReturn(intShopperAccountPaymentAccountFirstUseDateMock);
        when(shopperAccountPasswordChangeDateMock.transformToInternalModel()).thenReturn(intShopperAccountPasswordChangeDateMock);
    }

    @Test
    public void transformToInternalModel_ShouldAddAllMembersOfTheInternalObject_WhenTheyExist() {
        testObj.setShopperAccountAgeIndicator("shopperAccountAgeIndicator");
        testObj.setTransactionsAttemptedLastDay("transactionsAttemptedLastDay");
        testObj.setShopperAccountPaymentAccountIndicator("shopperAccountPaymentAccountIndicator");
        testObj.setShopperAccountChangeIndicator("shopperAccountChangeIndicator");
        testObj.setShopperAccountPasswordChangeIndicator("shopperAccountPasswordChangeIndicator");
        testObj.setPreviousSuspiciousActivity("previousSuspiciousActivity");
        testObj.setAddCardAttemptsLastDay("addCardAttemptsLastDay");
        testObj.setTransactionsAttemptedLastYear("transactionsAttemptedLastYear");
        testObj.setPurchasesCompletedLastSixMonths("purchasesCompletedLastSixMonths");
        testObj.setShopperAccountShippingAddressUsageIndicator("shopperAccountShippingAddressUsageIndicator");
        testObj.setShippingNameMatchesAccountName("shippingNameMatchesAccountName");

        final com.worldpay.internal.model.ShopperAccountRiskData result = testObj.transformToInternalModel();

        assertThat(result.getShopperAccountAgeIndicator()).isEqualTo("shopperAccountAgeIndicator");
        assertThat(result.getTransactionsAttemptedLastDay()).isEqualTo("transactionsAttemptedLastDay");
        assertThat(result.getShopperAccountPaymentAccountIndicator()).isEqualTo("shopperAccountPaymentAccountIndicator");
        assertThat(result.getShopperAccountChangeIndicator()).isEqualTo("shopperAccountChangeIndicator");
        assertThat(result.getShopperAccountPasswordChangeIndicator()).isEqualTo("shopperAccountPasswordChangeIndicator");
        assertThat(result.getPreviousSuspiciousActivity()).isEqualTo("previousSuspiciousActivity");
        assertThat(result.getAddCardAttemptsLastDay()).isEqualTo("addCardAttemptsLastDay");
        assertThat(result.getTransactionsAttemptedLastYear()).isEqualTo("transactionsAttemptedLastYear");
        assertThat(result.getPurchasesCompletedLastSixMonths()).isEqualTo("purchasesCompletedLastSixMonths");
        assertThat(result.getShopperAccountShippingAddressUsageIndicator()).isEqualTo("shopperAccountShippingAddressUsageIndicator");
        assertThat(result.getShippingNameMatchesAccountName()).isEqualTo("shippingNameMatchesAccountName");

        assertThat(result.getShopperAccountCreationDate().getDate()).isEqualTo(intShopperAccountCreationDateMock);
        assertThat(result.getShopperAccountModificationDate().getDate()).isEqualTo(intShopperAccountModificationDateMock);
        assertThat(result.getShopperAccountPasswordChangeDate().getDate()).isEqualTo(intShopperAccountPasswordChangeDateMock);
        assertThat(result.getShopperAccountShippingAddressFirstUseDate().getDate()).isEqualTo(intShopperAccountShippingAddressFirstUseDateMock);
        assertThat(result.getShopperAccountPaymentAccountFirstUseDate().getDate()).isEqualTo(intShopperAccountPaymentAccountFirstUseDateMock);
    }

    @Test
    public void transformToInternalModel_ShouldNotAddShopperAccountCreationDate() {
        testObj.setShopperAccountCreationDate(null);

        final com.worldpay.internal.model.ShopperAccountRiskData result = testObj.transformToInternalModel();

        assertThat(result.getShopperAccountCreationDate()).isNull();
    }

    @Test
    public void transformToInternalModel_ShouldNotAddShopperAccountShippingAddressFirstUseDate() {
        testObj.setShopperAccountShippingAddressFirstUseDate(null);

        final com.worldpay.internal.model.ShopperAccountRiskData result = testObj.transformToInternalModel();

        assertThat(result.getShopperAccountShippingAddressFirstUseDate()).isNull();
    }

    @Test
    public void transformToInternalModel_ShouldNotAddShopperAccountModificationDate() {
        testObj.setShopperAccountModificationDate(null);

        final com.worldpay.internal.model.ShopperAccountRiskData result = testObj.transformToInternalModel();

        assertThat(result.getShopperAccountModificationDate()).isNull();
    }

    @Test
    public void transformToInternalModel_ShouldNotAddShopperAccountPaymentAccountFirstUseDate() {
        testObj.setShopperAccountPaymentAccountFirstUseDate(null);

        final com.worldpay.internal.model.ShopperAccountRiskData result = testObj.transformToInternalModel();

        assertThat(result.getShopperAccountPaymentAccountFirstUseDate()).isNull();
    }

    @Test
    public void transformToInternalModel_ShouldNotAddShopperAccountPasswordChangeDate() {
        testObj.setShopperAccountPasswordChangeDate(null);

        final com.worldpay.internal.model.ShopperAccountRiskData result = testObj.transformToInternalModel();

        assertThat(result.getShopperAccountPasswordChangeDate()).isNull();
    }
}
