package com.worldpay.service.mac.impl;

import com.worldpay.enums.order.AuthorisedStatus;
import com.worldpay.exception.WorldpayMacValidationException;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

@UnitTest
public class HMAC256MacValidatorTest {

    @Rule
    @SuppressWarnings("PMD.MemberScope")
    public ExpectedException thrown = ExpectedException.none();

    private final HMAC256MacValidator testObj = new HMAC256MacValidator();

    @Test
    public void testSha256Valid() throws WorldpayMacValidationException {

        final boolean result = testObj.validateResponse("MYADMINCODE^MYMERCHANT^T0211010", "856ff737b2987f21513b91992818d983ce9fed97847b15756c56493a23090415", "1400", "GBP", AuthorisedStatus.AUTHORISED, "@p-p1epie");
        assertTrue("Mac validation code correct", result);
    }

    @Test
    public void testSha256Invalid() throws WorldpayMacValidationException {

        final boolean result = testObj.validateResponse("MYADMINCODE^MYMERCHANT^T0211010", "856ff737b2987f21513b91992818d983ce9fed97847b15756c56493a23090416", "1400", "GBP", AuthorisedStatus.AUTHORISED, "@p-p1epie");
        assertFalse("Mac validation code incorrect", result);
    }

    @Test
    public void testValidateResponseInvalidNulls() throws WorldpayMacValidationException {
        thrown.expect(WorldpayMacValidationException.class);

        testObj.validateResponse(null, null, null, null, null, null);
        fail("Mac validation should fail with caught exception");
    }

    @Test
    public void testValidateResponseInvalidPartialNulls() throws WorldpayMacValidationException {
        thrown.expect(WorldpayMacValidationException.class);

        boolean result = testObj.validateResponse(null, "25eefe952a6bbd09fe1c2c09bca4fa08", null, null, null, null);
        assertFalse("Mac validation code correct", result);
    }
}
