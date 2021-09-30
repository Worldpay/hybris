package com.worldpay.converters.populators.internal.model.klarna;

import com.worldpay.internal.model.KLARNASSL;
import com.worldpay.data.klarna.KlarnaMerchantUrls;
import com.worldpay.data.klarna.KlarnaPayment;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class KlarnaPaymentPopulatorTest {

    private static final String EXTRA_MERCHANT_DATA = "extraMerchantData";
    private static final String PURCHASE_COUNTRY = "purchaseCountry";
    private static final String SHOPPER_LOCALE = "shopperLocale";

    @InjectMocks
    private KlarnaPaymentPopulator testObj;

    @Mock
    private KlarnaPayment sourceMock;
    @Mock
    private KlarnaMerchantUrls klarnaMerchantUrlsMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNullA_ShouldThrowAnException() {
        testObj.populate(null, new KLARNASSL());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenKlarnaMerchantUrlsIsNull_ShouldNotPopulateMerchantUrls() {
        when(sourceMock.getMerchantUrls()).thenReturn(null);

        final KLARNASSL targetMock = new KLARNASSL();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getMerchantUrls()).isNull();
    }

    @Test
    public void populate_ShouldPopulateKlarnaPayment() {
        when(sourceMock.getPurchaseCountry()).thenReturn(PURCHASE_COUNTRY);
        when(sourceMock.getShopperLocale()).thenReturn(SHOPPER_LOCALE);
        when(sourceMock.getExtraMerchantData()).thenReturn(EXTRA_MERCHANT_DATA);
        when(sourceMock.getMerchantUrls()).thenReturn(klarnaMerchantUrlsMock);

        final KLARNASSL targetMock = new KLARNASSL();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getExtraMerchantData()).isEqualTo(EXTRA_MERCHANT_DATA);
        assertThat(targetMock.getPurchaseCountry()).isEqualTo(PURCHASE_COUNTRY);
        assertThat(targetMock.getShopperLocale()).isEqualTo(SHOPPER_LOCALE);
        assertThat(targetMock.getMerchantUrls().getCheckoutURL()).isEqualTo(klarnaMerchantUrlsMock.getCheckoutURL());
        assertThat(targetMock.getMerchantUrls().getConfirmationURL()).isEqualTo(klarnaMerchantUrlsMock.getConfirmationURL());
    }
}
