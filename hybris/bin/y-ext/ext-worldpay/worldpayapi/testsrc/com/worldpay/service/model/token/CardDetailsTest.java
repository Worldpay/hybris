package com.worldpay.service.model.token;

import com.worldpay.service.model.Address;
import com.worldpay.service.model.Date;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@UnitTest
public class CardDetailsTest {

    private static final String ISSUER_COUNTRY_CODE = "issuerContryCode";
    private static final String CARD_SUB_BRAND = "cardSubBrand";
    private static final String CARD_BRAND = "cardBrand";
    private static final String FIRST_NAME = "JJJJ";
    private static final String CARD_HOLDER_NAME = "cardHolderName";
    private static final String CVC_NUMBER = "123";
    private static final String CARD_NUMBER = "cardNumber";

    @Test
    public void shouldTransformToInternalCardDetails() {
        final CardDetails cardDetails = new CardDetails();
        cardDetails.setIssuerCountryCode(ISSUER_COUNTRY_CODE);
        cardDetails.setCardSubBrand(CARD_SUB_BRAND);
        cardDetails.setCardBrand(CARD_BRAND);
        final Address cardAddress = new Address();
        cardAddress.setFirstName(FIRST_NAME);
        cardDetails.setCardAddress(cardAddress);
        cardDetails.setCardHolderName(CARD_HOLDER_NAME);
        cardDetails.setCvcNumber(CVC_NUMBER);
        cardDetails.setCardNumber(CARD_NUMBER);
        final Date expiryDate = new Date(LocalDateTime.now().plusYears(2));
        cardDetails.setExpiryDate(expiryDate);

        final com.worldpay.internal.model.CardDetails result = (com.worldpay.internal.model.CardDetails) cardDetails.transformToInternalModel();

        assertEquals(ISSUER_COUNTRY_CODE, result.getDerived().getIssuerCountryCode());
        assertEquals(CARD_SUB_BRAND, result.getDerived().getCardSubBrand());
        assertEquals(CARD_BRAND, result.getDerived().getCardBrand());
        assertEquals(FIRST_NAME, result.getCardAddress().getAddress().getFirstName());
        assertEquals(CARD_HOLDER_NAME, result.getCardHolderName().getvalue());
        assertEquals(CVC_NUMBER, result.getCvc().getvalue());
        assertEquals(CARD_NUMBER, result.getDerived().getObfuscatedPAN());
        assertEquals(expiryDate.getMonth(), result.getExpiryDate().getDate().getMonth());
        assertEquals(expiryDate.getYear(), result.getExpiryDate().getDate().getYear());
    }

    @Test
    public void shouldNotTransformEmptyFieldsToInternalCardDetails() {
        final CardDetails cardDetails = new CardDetails();

        final com.worldpay.internal.model.CardDetails result = (com.worldpay.internal.model.CardDetails) cardDetails.transformToInternalModel();

        assertNull(result.getDerived());
        assertNull(result.getCardAddress());
        assertNull(result.getCardHolderName());
        assertNull(result.getCvc());
        assertNull(result.getDerived());
        assertNull(result.getExpiryDate());
    }
}
