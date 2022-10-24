package com.worldpay.converters.populators.internal.model.payment;

import com.worldpay.enums.payment.storedCredentials.MerchantInitiatedReason;
import com.worldpay.enums.payment.storedCredentials.Usage;
import com.worldpay.data.payment.StoredCredentials;
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
public class StoredCredentialsPopulatorTest {

    private static final String SCHEME_TRANSACTION_ID = "SchemeTransactionId";

    @InjectMocks
    private StoredCredentialsPopulator testObj;

    @Mock
    private StoredCredentials sourceMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.StoredCredentials());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetUsageIsNull_ShouldNotPopulateUsage() {
        when(sourceMock.getUsage()).thenReturn(null);

        final com.worldpay.internal.model.StoredCredentials target = new com.worldpay.internal.model.StoredCredentials();
        testObj.populate(sourceMock, target);

        assertThat(target.getUsage()).isNull();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getMerchantInitiatedReason()).thenReturn(MerchantInitiatedReason.DELAYED);
        when(sourceMock.getUsage()).thenReturn(Usage.FIRST);
        when(sourceMock.getSchemeTransactionIdentifier()).thenReturn(SCHEME_TRANSACTION_ID);

        final com.worldpay.internal.model.StoredCredentials target = new com.worldpay.internal.model.StoredCredentials();
        testObj.populate(sourceMock, target);

        assertThat(target.getMerchantInitiatedReason()).isEqualTo(MerchantInitiatedReason.DELAYED.name());
        assertThat(target.getUsage()).isEqualTo(Usage.FIRST.name());
        assertThat(target.getSchemeTransactionIdentifier()).isEqualTo(SCHEME_TRANSACTION_ID);
    }
}
