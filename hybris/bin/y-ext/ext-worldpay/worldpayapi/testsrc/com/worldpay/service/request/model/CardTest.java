package com.worldpay.service.request.model;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.internal.model.VISASSL;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.Date;
import com.worldpay.service.model.payment.Card;
import com.worldpay.service.model.payment.PaymentType;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@UnitTest
public class CardTest {

    private static final String CARDHOLDER_NAME = "John Smith";
    private static final String CARD_NUMBER = "4111111111111111";
    private static final String CVC = "123";
    private static final String FIRST_NAME = "firstName";

    /**
     * Test method for {@link com.worldpay.service.model.payment.Card#transformToInternalModel()}.
     */
    @Test
    public void testTransformToInternalModel() throws WorldpayModelTransformationException {
        final Address address = new Address();
        address.setFirstName(FIRST_NAME);
        final Date expiryDate = new Date("01", "1974");
        final Card card = new Card(PaymentType.VISA, CARD_NUMBER, CVC, expiryDate, CARDHOLDER_NAME, address, null, null, null);

        final VISASSL result = (VISASSL) card.transformToInternalModel();

        assertEquals(CARD_NUMBER, result.getCardNumber().getvalue());
        assertEquals(CARDHOLDER_NAME, result.getCardHolderName().getvalue());
        assertEquals(CVC, result.getCvc().getvalue());
        assertEquals(expiryDate.getMonth(), result.getExpiryDate().getDate().getMonth());
        assertEquals(expiryDate.getYear(), result.getExpiryDate().getDate().getYear());
        assertEquals(FIRST_NAME, result.getCardAddress().getAddress().getFirstName());
    }
}
