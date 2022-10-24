package com.worldpay.converters.populators.internal.model;

import com.worldpay.data.PaymentMethodMask;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.service.model.payment.PaymentType;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodMaskPopulatorTest {

    @InjectMocks
    private PaymentMethodMaskPopulator testObj;

    @Mock
    private Converter<StoredCredentials, com.worldpay.internal.model.StoredCredentials> internalStoredCredentialsConverterMock;

    @Mock
    private PaymentMethodMask sourceMock;
    @Mock
    private StoredCredentials storedCredentialsMock;
    @Mock
    private com.worldpay.internal.model.StoredCredentials intStoredCredentialsMock;

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.PaymentMethodMask());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetStoredCredentialsIsNull_ShouldNotPopulateStoredCredentials() {
        when(sourceMock.getStoredCredentials()).thenReturn(null);

        final com.worldpay.internal.model.PaymentMethodMask target = new com.worldpay.internal.model.PaymentMethodMask();
        testObj.populate(sourceMock, target);

        assertThat(target.getStoredCredentialsOrIncludeOrExclude()).isEmpty();
    }

    @Test
    public void populate_WhenGetExcludesIsNull_ShouldNotPopulateExcludes() {
        when(sourceMock.getExcludes()).thenReturn(null);

        final com.worldpay.internal.model.PaymentMethodMask target = new com.worldpay.internal.model.PaymentMethodMask();
        testObj.populate(sourceMock, target);

        assertThat(target.getStoredCredentialsOrIncludeOrExclude()).isEmpty();
    }

    @Test
    public void populate_WhenGetIncludesIsNull_ShouldNotPopulateIncludes() {
        when(sourceMock.getIncludes()).thenReturn(null);

        final com.worldpay.internal.model.PaymentMethodMask target = new com.worldpay.internal.model.PaymentMethodMask();
        testObj.populate(sourceMock, target);

        assertThat(target.getStoredCredentialsOrIncludeOrExclude()).isEmpty();
    }

    @Test
    public void populate_WhenSourceAndTargetAreNotNullAndAllTheFieldsAreNotNull_ShouldPopulate() {
        when(sourceMock.getStoredCredentials()).thenReturn(storedCredentialsMock);
        when(internalStoredCredentialsConverterMock.convert(storedCredentialsMock)).thenReturn(intStoredCredentialsMock);
        when(sourceMock.getExcludes()).thenReturn(List.of(PaymentType.ONLINE.getMethodCode(), PaymentType.EWIRESE.getMethodCode()));
        when(sourceMock.getIncludes()).thenReturn(List.of(PaymentType.ALIPAY.getMethodCode(), PaymentType.LOBANET_PE.getMethodCode()));

        final com.worldpay.internal.model.PaymentMethodMask target = new com.worldpay.internal.model.PaymentMethodMask();
        testObj.populate(sourceMock, target);

        assertThat(target.getStoredCredentialsOrIncludeOrExclude()).hasSize(5);
    }
}
