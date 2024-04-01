package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.data.payment.AlternativePayment;
import com.worldpay.enums.PayPalIntent;
import com.worldpay.internal.model.PAYPALSSL;
import com.worldpay.service.model.payment.PaymentType;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
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
public class PayPalSSLPaymentPopulatorTest {

    private static final String CANCEL_URL = "cancelURL";
    private static final String FAILURE_URL = "failureURL";
    private static final String PENDING_URL = "pendingURL";
    private static final String SUCCESS_URL = "successURL";

    @InjectMocks
    private PayPalSSLPaymentPopulator testObj;

    @Mock
    private BaseSiteService baseSiteServiceMock;
    @Mock
    private BaseSiteModel baseSiteModelMock;

    @Before
    public void setUp() {
        when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(baseSiteModelMock);
        when(baseSiteModelMock.getPaypalSSLIntent()).thenReturn(PayPalIntent.AUTHORISE);
    }

    @Test
    public void populateShouldPopulateURLsAndAuthoriseIntent() {
        final AlternativePayment source = new AlternativePayment();
        source.setCancelURL(CANCEL_URL);
        source.setFailureURL(FAILURE_URL);
        source.setPendingURL(PENDING_URL);
        source.setSuccessURL(SUCCESS_URL);
        source.setPaymentType(PaymentType.PAYPAL_SSL.getMethodCode());

        when(baseSiteModelMock.getPaypalSSLIntent()).thenReturn(PayPalIntent.AUTHORISE);

        final PAYPALSSL target = new PAYPALSSL();

        // Execute
        testObj.populate(source, target);

        // Verify
        assertThat(target.getIntent()).isEqualTo(PayPalIntent.AUTHORISE.toString().toLowerCase());
        assertThat(target.getCancelURL()).isEqualTo(CANCEL_URL);
        assertThat(target.getFailureURL()).isEqualTo(FAILURE_URL);
        assertThat(target.getPendingURL()).isEqualTo(PENDING_URL);
        assertThat(target.getSuccessURL()).isEqualTo(SUCCESS_URL);
    }

    @Test
    public void populateShouldPopulateURLsAndCaptureIntent() {
        when(baseSiteModelMock.getPaypalSSLIntent()).thenReturn(PayPalIntent.CAPTURE);

        final AlternativePayment source = new AlternativePayment();
        source.setCancelURL(CANCEL_URL);
        source.setFailureURL(FAILURE_URL);
        source.setPendingURL(PENDING_URL);
        source.setSuccessURL(SUCCESS_URL);

        final PAYPALSSL target = new PAYPALSSL();

        // Execute
        testObj.populate(source, target);

        // Verify
        assertThat(target.getIntent()).isEqualTo(PayPalIntent.CAPTURE.toString().toLowerCase());
        assertThat(target.getCancelURL()).isEqualTo(CANCEL_URL);
        assertThat(target.getFailureURL()).isEqualTo(FAILURE_URL);
        assertThat(target.getPendingURL()).isEqualTo(PENDING_URL);
        assertThat(target.getSuccessURL()).isEqualTo(SUCCESS_URL);
    }

}