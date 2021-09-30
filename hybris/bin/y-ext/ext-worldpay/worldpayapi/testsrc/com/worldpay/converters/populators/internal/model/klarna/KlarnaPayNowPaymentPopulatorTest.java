package com.worldpay.converters.populators.internal.model.klarna;

import com.worldpay.internal.model.KLARNAPAYNOWSSL;
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
public class KlarnaPayNowPaymentPopulatorTest {

    private static final String SHOPPER_COUNTRY_CODE = "shopperCountryCode";
    private static final String LOCALE = "locale";
    private static final String SUCCESS_URL = "SuccessURL";
    private static final String PENDING_URL = "pendingURL";
    private static final String FAILURE_URL = "FailureURL";
    private static final String CANCEL_URL = "cancelURL";

    @InjectMocks
    private KlarnaPayNowPaymentPopulator testObj;

    @Mock
    private KlarnaPayment sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new KLARNAPAYNOWSSL());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_ShouldPopulateKlarnaPayment() {
        when(sourceMock.getShopperCountryCode()).thenReturn(SHOPPER_COUNTRY_CODE);
        when(sourceMock.getLocale()).thenReturn(LOCALE);
        when(sourceMock.getSuccessURL()).thenReturn(SUCCESS_URL);
        when(sourceMock.getPendingURL()).thenReturn(PENDING_URL);
        when(sourceMock.getFailureURL()).thenReturn(FAILURE_URL);
        when(sourceMock.getCancelURL()).thenReturn(CANCEL_URL);

        final KLARNAPAYNOWSSL targetMock = new KLARNAPAYNOWSSL();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getShopperCountryCode()).isEqualTo(SHOPPER_COUNTRY_CODE);
        assertThat(targetMock.getLocale()).isEqualTo(LOCALE);
        assertThat(targetMock.getSuccessURL()).isEqualTo(SUCCESS_URL);
        assertThat(targetMock.getFailureURL()).isEqualTo(FAILURE_URL);
        assertThat(targetMock.getCancelURL()).isEqualTo(CANCEL_URL);
        assertThat(targetMock.getPendingURL()).isEqualTo(PENDING_URL);
    }
}
