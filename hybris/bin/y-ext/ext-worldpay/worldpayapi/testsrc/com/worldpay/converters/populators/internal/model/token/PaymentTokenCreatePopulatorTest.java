package com.worldpay.converters.populators.internal.model.token;

import com.worldpay.converters.internal.model.payment.PaymentConverterStrategy;
import com.worldpay.internal.model.CreateToken;
import com.worldpay.data.payment.Payment;
import com.worldpay.data.payment.StoredCredentials;
import com.worldpay.data.token.CardTokenRequest;
import com.worldpay.data.token.TokenRequest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentTokenCreatePopulatorTest {

    private static final String SHOPPER_ID = "shopperId";

    @InjectMocks
    private PaymentTokenCreatePopulator testObj;

    @Mock
    private Converter<TokenRequest, CreateToken> internalTokenRequestConverter;
    @Mock
    private Converter<StoredCredentials, com.worldpay.internal.model.StoredCredentials> internalStoredCredentialsConverter;
    @Mock
    private PaymentConverterStrategy internalPaymentConverterStrategy;

    @Mock
    private CardTokenRequest sourceMock;
    @Mock
    private StoredCredentials storeCredentialsMock;
    @Mock
    private com.worldpay.internal.model.StoredCredentials intStoreCredentialsMock;
    @Mock
    private TokenRequest tokenRequestMock;
    @Mock
    private CreateToken createTokenMock;
    @Mock
    private Payment paymentMock;
    @Mock
    private Object intPaymentMock;

    @Before
    public void setup() {
        testObj = new PaymentTokenCreatePopulator(internalTokenRequestConverter, internalStoredCredentialsConverter, internalPaymentConverterStrategy);
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.PaymentTokenCreate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenGetAuthenticatedShopperIdIsNull_ShouldNotPopulateAuthenticatedShopperId() {
        when(sourceMock.getAuthenticatedShopperId()).thenReturn(null);

        final com.worldpay.internal.model.PaymentTokenCreate targetMock = new com.worldpay.internal.model.PaymentTokenCreate();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAuthenticatedShopperID()).isNull();
    }

    @Test
    public void populate_WhenGetTokenRequestIsNull_ShouldNotPopulateTokenRequest() {
        when(sourceMock.getTokenRequest()).thenReturn(null);

        final com.worldpay.internal.model.PaymentTokenCreate targetMock = new com.worldpay.internal.model.PaymentTokenCreate();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getCreateToken()).isNull();
    }

    @Test
    public void populate_WhenGetStoredCredentialsIsNull_ShouldNotPopulateStoredCredentials() {
        when(sourceMock.getStoredCredentials()).thenReturn(null);

        final com.worldpay.internal.model.PaymentTokenCreate targetMock = new com.worldpay.internal.model.PaymentTokenCreate();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getStoredCredentials()).isNull();
    }

    @Test
    public void populate_WhenGetPaymentIsNull_ShouldNotPopulatePaymentInstrumentOrCSEDATA() {
        when(sourceMock.getPayment()).thenReturn(null);

        final com.worldpay.internal.model.PaymentTokenCreate targetMock = new com.worldpay.internal.model.PaymentTokenCreate();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getPaymentInstrumentOrCSEDATA()).isEmpty();
    }

    @Test
    public void populate_ShouldPopulatePaymentTokenCreate() {
        when(sourceMock.getAuthenticatedShopperId()).thenReturn(SHOPPER_ID);
        when(sourceMock.getStoredCredentials()).thenReturn(storeCredentialsMock);
        when(sourceMock.getTokenRequest()).thenReturn(tokenRequestMock);
        when(sourceMock.getPayment()).thenReturn(paymentMock);

        when(internalTokenRequestConverter.convert(tokenRequestMock)).thenReturn(createTokenMock);
        when(internalStoredCredentialsConverter.convert(storeCredentialsMock)).thenReturn(intStoreCredentialsMock);
        when(internalPaymentConverterStrategy.convertPayment(paymentMock)).thenReturn(intPaymentMock);

        final com.worldpay.internal.model.PaymentTokenCreate targetMock = new com.worldpay.internal.model.PaymentTokenCreate();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getAuthenticatedShopperID().getvalue()).isEqualTo(SHOPPER_ID);
        assertThat(targetMock.getCreateToken()).isEqualTo(createTokenMock);
        assertThat(targetMock.getStoredCredentials()).isEqualTo(intStoreCredentialsMock);
        assertThat(targetMock.getPaymentInstrumentOrCSEDATA().get(0)).isEqualTo(intPaymentMock);
    }
}
