package com.worldpay.converters.populators.internal.model.token;

import com.worldpay.data.Address;
import com.worldpay.data.Date;
import com.worldpay.data.token.CardDetails;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CardDetailsPopulatorTest {

    private static final String CARD_BRAND = "cardBrand";
    private static final String CARD_HOLDER_NAME = "cardHolderName";
    private static final String CARD_NUMBER = "cardNumber";
    private static final String CARD_SUB_BRAND = "cardSubBrand";
    private static final String CVC_NUMBER = "cvcNumber";
    private static final String ISSUER_COUNTRY_CODE = "issuerCountryCode";

    @InjectMocks
    private CardDetailsPopulator testObj;

    @Mock
    private Converter<Address, com.worldpay.internal.model.Address> internalAddressConverterMock;
    @Mock
    private Converter<com.worldpay.data.Date, com.worldpay.internal.model.Date> internalDateConverterMock;

    @Mock
    private CardDetails sourceMock;
    @Mock
    private Address cardAddresMock;
    @Mock
    private Date expiryDateMock;
    @Mock
    private com.worldpay.internal.model.Address internalAddresMock;
    @Mock
    private com.worldpay.internal.model.Date internalDateMock;

    @Before
    public void setup() {
        testObj = new CardDetailsPopulator(internalAddressConverterMock, internalDateConverterMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenSourceIsNull_ShouldThrowAnException() {
        testObj.populate(null, new com.worldpay.internal.model.CardDetails());
    }

    @Test(expected = IllegalArgumentException.class)
    public void populate_WhenTargetIsNull_ShouldThrowAnException() {
        testObj.populate(sourceMock, null);
    }

    @Test
    public void populate_WhenDateIsNull_ShouldNotPopulateInternalDate() {
        when(sourceMock.getExpiryDate()).thenReturn(null);
        lenient().when(internalDateConverterMock.convert(sourceMock.getExpiryDate())).thenReturn(internalDateMock);

        final com.worldpay.internal.model.CardDetails targetMock = new com.worldpay.internal.model.CardDetails();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getExpiryDate()).isNull();
    }

    @Test
    public void populate_WhenAddressIsNull_ShouldNotPopulateInternalAddress() {
        when(sourceMock.getCardAddress()).thenReturn(null);
        lenient().when(internalAddressConverterMock.convert(sourceMock.getCardAddress())).thenReturn(internalAddresMock);

        final com.worldpay.internal.model.CardDetails targetMock = new com.worldpay.internal.model.CardDetails();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getCardAddress()).isNull();
    }

    @Test
    public void populate_ShouldPopulateCardDetails() {
        when(sourceMock.getCardAddress()).thenReturn(cardAddresMock);
        when(sourceMock.getCardBrand()).thenReturn(CARD_BRAND);
        when(sourceMock.getCardHolderName()).thenReturn(CARD_HOLDER_NAME);
        when(sourceMock.getCardNumber()).thenReturn(CARD_NUMBER);
        when(sourceMock.getCardSubBrand()).thenReturn(CARD_SUB_BRAND);
        when(sourceMock.getCvcNumber()).thenReturn(CVC_NUMBER);
        when(sourceMock.getExpiryDate()).thenReturn(expiryDateMock);
        when(sourceMock.getIssuerCountryCode()).thenReturn(ISSUER_COUNTRY_CODE);

        lenient().when(internalAddressConverterMock.convert(cardAddresMock)).thenReturn(internalAddresMock);
        lenient().when(internalDateConverterMock.convert(expiryDateMock)).thenReturn(internalDateMock);

        final com.worldpay.internal.model.CardDetails targetMock = new com.worldpay.internal.model.CardDetails();
        testObj.populate(sourceMock, targetMock);

        assertThat(targetMock.getCardAddress().getAddress()).isEqualTo(internalAddresMock);
        assertThat(targetMock.getCardHolderName().getvalue()).isEqualTo(CARD_HOLDER_NAME);
        assertThat(targetMock.getCvc().getvalue()).isEqualTo(CVC_NUMBER);
        assertThat(targetMock.getDerived().getObfuscatedPAN()).isEqualTo(CARD_NUMBER);
        assertThat(targetMock.getExpiryDate().getDate()).isEqualTo(internalDateMock);
    }
}
