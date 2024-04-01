package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.internal.model.IDEALSSL;
import com.worldpay.data.payment.AlternativeShopperBankCodePayment;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IdealPaymentPopulatorTest {

    private static final String SUCCESS_URL = "successURL";
    private static final String FAILURE_URL = "failureURL";
    private static final String CANCEL_URL = "cancelURL";
    private static final String PENDING_URL = "pendingURL";

    @InjectMocks
    private IdealPaymentPopulator testObj;

    @Mock
    private AlternativeShopperBankCodePayment sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.IDEALSSL());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetSuccessURLIsNull_ShouldNotPopulateSuccessURL() {
        when(sourceMock.getSuccessURL()).thenReturn(null);

        final IDEALSSL target = new IDEALSSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getSuccessURL()).isNull();
    }

    @Test
    public void populate_WhenGetFailureURLIsNull_ShouldNotPopulateFailureURL() {
        when(sourceMock.getFailureURL()).thenReturn(null);

        final IDEALSSL target = new IDEALSSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getFailureURL()).isNull();
    }

    @Test
    public void populate_WhenGetCancelURLIsNull_ShouldNotPopulateCancelURL() {
        when(sourceMock.getCancelURL()).thenReturn(null);

        final IDEALSSL target = new IDEALSSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getCancelURL()).isNull();
    }

    @Test
    public void populate_WhenGetPendingURLIsNull_ShouldNotPopulatePendingURL() {
        when(sourceMock.getPendingURL()).thenReturn(null);

        final IDEALSSL target = new IDEALSSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getPendingURL()).isNull();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getSuccessURL()).thenReturn(SUCCESS_URL);
        when(sourceMock.getFailureURL()).thenReturn(FAILURE_URL);
        when(sourceMock.getCancelURL()).thenReturn(CANCEL_URL);
        when(sourceMock.getPendingURL()).thenReturn(PENDING_URL);

        final IDEALSSL target = new IDEALSSL();
        testObj.populate(sourceMock, target);

        assertThat(target.getSuccessURL()).isEqualTo(SUCCESS_URL);
        assertThat(target.getFailureURL()).isEqualTo(FAILURE_URL);
        assertThat(target.getCancelURL()).isEqualTo(CANCEL_URL);
        assertThat(target.getPendingURL()).isEqualTo(PENDING_URL);
    }
}
